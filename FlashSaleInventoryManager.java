import java.util.*;
import java.util.concurrent.*;

public class FlashSaleInventoryManager {

    private ConcurrentHashMap<String, Integer> stockMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Queue<Integer>> waitingList = new ConcurrentHashMap<>();

    public FlashSaleInventoryManager() {
        stockMap.put("IPHONE15_256GB", 100);
        waitingList.put("IPHONE15_256GB", new ConcurrentLinkedQueue<>());
    }

    public int checkStock(String productId) {
        return stockMap.getOrDefault(productId, 0);
    }

    public String purchaseItem(String productId, int userId) {
        synchronized (productId.intern()) {
            int stock = stockMap.getOrDefault(productId, 0);

            if (stock > 0) {
                stockMap.put(productId, stock - 1);
                return "Success, " + (stock - 1) + " units remaining";
            } else {
                Queue<Integer> queue = waitingList.get(productId);
                if (queue == null) {
                    queue = new ConcurrentLinkedQueue<>();
                    waitingList.put(productId, queue);
                }
                queue.add(userId);
                return "Added to waiting list, position #" + queue.size();
            }
        }
    }

    public List<Integer> getWaitingList(String productId) {
        Queue<Integer> queue = waitingList.get(productId);
        if (queue == null) return new ArrayList<>();
        return new ArrayList<>(queue);
    }

    public static void main(String[] args) {

        FlashSaleInventoryManager manager = new FlashSaleInventoryManager();

        System.out.println("Stock: " + manager.checkStock("IPHONE15_256GB"));

        System.out.println(manager.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 67890));

        for (int i = 0; i < 100; i++) {
            manager.purchaseItem("IPHONE15_256GB", i);
        }

        System.out.println(manager.purchaseItem("IPHONE15_256GB", 99999));

        System.out.println("Waiting List: " + manager.getWaitingList("IPHONE15_256GB"));
    }
}