import java.util.*;
import java.util.concurrent.*;
class PageViewEvent {
    String url;
    String userId;
    String source;
    PageViewEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}
public class RealTimeAnalytics {
    private ConcurrentHashMap<String, Integer> pageViews = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Set<String>> uniqueVisitors = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> trafficSources = new ConcurrentHashMap<>();
    public void processEvent(PageViewEvent event) {
        pageViews.merge(event.url, 1, Integer::sum);
        uniqueVisitors
                .computeIfAbsent(event.url, k -> ConcurrentHashMap.newKeySet())
                .add(event.userId);
        trafficSources.merge(event.source, 1, Integer::sum);
    }
    public void getDashboard() {
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());
        pq.addAll(pageViews.entrySet());
        System.out.println("Top Pages:");
        int count = 0;
        while (!pq.isEmpty() && count < 10) {
            Map.Entry<String, Integer> entry = pq.poll();
            String url = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.getOrDefault(url, Collections.emptySet()).size();
            System.out.println((count + 1) + ". " + url + " - " + views + " views (" + unique + " unique)");
            count++;
        }
        System.out.println("\nTraffic Sources:");
        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {
            System.out.println(entry.getKey() + " → " + entry.getValue());
        }
    }
    public static void main(String[] args) throws Exception {
        RealTimeAnalytics analytics = new RealTimeAnalytics();
        analytics.processEvent(new PageViewEvent("/article/breaking-news", "user_123", "google"));
        analytics.processEvent(new PageViewEvent("/article/breaking-news", "user_456", "facebook"));
        analytics.processEvent(new PageViewEvent("/sports/championship", "user_789", "google"));
        analytics.processEvent(new PageViewEvent("/sports/championship", "user_123", "direct"));
        analytics.processEvent(new PageViewEvent("/sports/championship", "user_999", "google"));
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("\n===== DASHBOARD UPDATE =====");
            analytics.getDashboard();
        }, 0, 5, TimeUnit.SECONDS);
    }
}