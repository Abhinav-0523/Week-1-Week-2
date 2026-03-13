import java.util.concurrent.*;

class TokenBucket {

    private int tokens;
    private final int maxTokens;
    private final double refillRate;
    private long lastRefillTime;

    public TokenBucket(int maxTokens, double refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.tokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }

    private void refill() {
        long now = System.currentTimeMillis();
        double tokensToAdd = (now - lastRefillTime) / 1000.0 * refillRate;
        if (tokensToAdd > 0) {
            tokens = Math.min(maxTokens, tokens + (int) tokensToAdd);
            lastRefillTime = now;
        }
    }

    public synchronized boolean allowRequest() {
        refill();
        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }

    public synchronized int getRemainingTokens() {
        refill();
        return tokens;
    }

    public synchronized long getRetryAfterSeconds() {
        refill();
        if (tokens > 0) return 0;
        return (long) Math.ceil(1.0 / refillRate);
    }
}

public class RateLimiter {

    private ConcurrentHashMap<String, TokenBucket> clientBuckets = new ConcurrentHashMap<>();

    private final int MAX_REQUESTS = 1000;
    private final double REFILL_RATE = 1000.0 / 3600.0;

    public boolean checkRateLimit(String clientId) {

        TokenBucket bucket = clientBuckets.computeIfAbsent(
                clientId,
                id -> new TokenBucket(MAX_REQUESTS, REFILL_RATE)
        );

        boolean allowed = bucket.allowRequest();

        if (allowed) {
            System.out.println("Allowed (" + bucket.getRemainingTokens() + " requests remaining)");
        } else {
            System.out.println("Denied (0 requests remaining, retry after " +
                    bucket.getRetryAfterSeconds() + "s)");
        }

        return allowed;
    }

    public void getRateLimitStatus(String clientId) {

        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket == null) {
            System.out.println("{used: 0, limit: " + MAX_REQUESTS + ", reset: 3600}");
            return;
        }

        int remaining = bucket.getRemainingTokens();
        int used = MAX_REQUESTS - remaining;

        System.out.println("{used: " + used +
                ", limit: " + MAX_REQUESTS +
                ", reset: 3600}");
    }

    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        for (int i = 0; i < 5; i++) {
            limiter.checkRateLimit("abc123");
        }

        limiter.getRateLimitStatus("abc123");
    }
}