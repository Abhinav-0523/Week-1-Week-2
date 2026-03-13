import java.util.*;

class MultiLevelCache {

    private int L1_CAP = 10000;
    private int L2_CAP = 100000;

    LinkedHashMap<String,String> L1 =
            new LinkedHashMap<String,String>(L1_CAP,0.75f,true){
                protected boolean removeEldestEntry(Map.Entry<String,String> e){
                    return size()>L1_CAP;
                }
            };

    LinkedHashMap<String,String> L2 =
            new LinkedHashMap<String,String>(L2_CAP,0.75f,true){
                protected boolean removeEldestEntry(Map.Entry<String,String> e){
                    return size()>L2_CAP;
                }
            };

    HashMap<String,String> database = new HashMap<>();

    int L1Hits=0, L2Hits=0, L3Hits=0;

    public void addVideo(String id,String data){
        database.put(id,data);
    }

    public String getVideo(String id){

        if(L1.containsKey(id)){
            L1Hits++;
            System.out.println("L1 Cache HIT");
            return L1.get(id);
        }

        if(L2.containsKey(id)){
            L2Hits++;
            System.out.println("L2 Cache HIT → Promoted to L1");

            String data = L2.get(id);
            L1.put(id,data);

            return data;
        }

        if(database.containsKey(id)){
            L3Hits++;

            System.out.println("L3 Database HIT → Added to L2");

            String data = database.get(id);
            L2.put(id,data);

            return data;
        }

        return null;
    }

    public void getStatistics(){

        int total = L1Hits+L2Hits+L3Hits;

        System.out.println("L1 Hit Rate: "+(L1Hits*100.0/total)+"%");
        System.out.println("L2 Hit Rate: "+(L2Hits*100.0/total)+"%");
        System.out.println("L3 Hit Rate: "+(L3Hits*100.0/total)+"%");
    }
}

public class PS10 {

    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        cache.addVideo("video_123","Movie Data");
        cache.addVideo("video_999","Other Movie");

        cache.getVideo("video_123");
        cache.getVideo("video_123");

        cache.getVideo("video_999");

        cache.getStatistics();
    }
}