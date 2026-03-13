import java.util.*;
class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;
    DNSEntry(String domain, String ipAddress, long ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + ttlSeconds * 1000;
    }
    boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}
public class DNSCache {
    private final int capacity;
    private int hits = 0;
    private int misses = 0;
    private LinkedHashMap<String, DNSEntry> cache;
    public DNSCache(int capacity) {
        this.capacity = capacity;
        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCache.this.capacity;
            }
        };
        startCleanupThread();
    }
    public synchronized String resolve(String domain) {
        DNSEntry entry = cache.get(domain);
        if (entry != null) {
            if (!entry.isExpired()) {
                hits++;
                return "Cache HIT → " + entry.ipAddress;
            } else {
                cache.remove(domain);
            }
        }
        misses++;
        String ip = queryUpstreamDNS(domain);
        cache.put(domain, new DNSEntry(domain, ip, 300));
        return "Cache MISS → Query upstream → " + ip + " (TTL: 300s)";
    }
    private String queryUpstreamDNS(String domain) {
        Random rand = new Random();
        return "172.217.14." + rand.nextInt(255);
    }
    public synchronized String getCacheStats() {
        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0) / total;
        return "Hit Rate: " + String.format("%.2f", hitRate) + "%";
    }
    private void startCleanupThread() {
        Thread cleaner = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    synchronized (this) {
                        Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<String, DNSEntry> entry = it.next();
                            if (entry.getValue().isExpired()) {
                                it.remove();
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        cleaner.setDaemon(true);
        cleaner.start();
    }
    public static void main(String[] args) throws Exception {
        DNSCache dnsCache = new DNSCache(5);
        System.out.println(dnsCache.resolve("google.com"));
        System.out.println(dnsCache.resolve("google.com"));
        Thread.sleep(2000);
        System.out.println(dnsCache.resolve("openai.com"));
        System.out.println(dnsCache.resolve("google.com"));
        System.out.println(dnsCache.getCacheStats());
    }
}