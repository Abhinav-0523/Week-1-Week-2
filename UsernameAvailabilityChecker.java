import java.util.*;
import java.util.concurrent.*;
public class UsernameAvailabilityChecker {
    private ConcurrentHashMap<String, Integer> usernameToUserId = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> attemptFrequency = new ConcurrentHashMap<>();
    public UsernameAvailabilityChecker() {
        usernameToUserId.put("john_doe", 1);
        usernameToUserId.put("admin", 2);
        usernameToUserId.put("testuser", 3);
    }
    public boolean checkAvailability(String username) {
        attemptFrequency.merge(username, 1, Integer::sum);
        return !usernameToUserId.containsKey(username);
    }
    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String suggestion = username + i;
            if (!usernameToUserId.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }
        String modified = username.replace("_", ".");
        if (!usernameToUserId.containsKey(modified)) {
            suggestions.add(modified);
        }
        return suggestions;
    }
    public String getMostAttempted() {
        return attemptFrequency.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
    public void registerUser(String username, int userId) {
        usernameToUserId.put(username, userId);
    }
    public static void main(String[] args) {
        UsernameAvailabilityChecker checker = new UsernameAvailabilityChecker();
        System.out.println(checker.checkAvailability("john_doe"));
        System.out.println(checker.checkAvailability("jane_smith"));
        System.out.println(checker.suggestAlternatives("john_doe"));
        checker.checkAvailability("admin");
        checker.checkAvailability("admin");
        checker.checkAvailability("admin");
        System.out.println(checker.getMostAttempted());
    }
}