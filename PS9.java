import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    long time;

    Transaction(int id, int amount, String merchant) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.time = System.currentTimeMillis();
    }
}

class TransactionAnalyzer {

    List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    // Classic Two Sum
    public void findTwoSum(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                System.out.println("Pair Found: " +
                        map.get(complement).id + " , " + t.id);
            }

            map.put(t.amount, t);
        }
    }

    // Duplicate detection
    public void detectDuplicates() {

        HashMap<String, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            String key = t.amount + "-" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t);
        }

        for (String key : map.keySet()) {

            if (map.get(key).size() > 1) {

                System.out.println("Duplicate detected for " + key);
            }
        }
    }
}

public class PS9 {

    public static void main(String[] args) {

        TransactionAnalyzer analyzer = new TransactionAnalyzer();

        analyzer.addTransaction(new Transaction(1, 500, "StoreA"));
        analyzer.addTransaction(new Transaction(2, 300, "StoreB"));
        analyzer.addTransaction(new Transaction(3, 200, "StoreC"));
        analyzer.addTransaction(new Transaction(4, 500, "StoreA"));

        analyzer.findTwoSum(500);

        analyzer.detectDuplicates();
    }
}