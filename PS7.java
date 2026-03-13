import java.util.*;

class TrieNode {

    HashMap<Character, TrieNode> children = new HashMap<>();
    boolean isWord = false;
}

class AutocompleteSystem {

    TrieNode root = new TrieNode();

    HashMap<String,Integer> frequency = new HashMap<>();

    public void insert(String query) {

        TrieNode node = root;

        for(char c : query.toCharArray()) {

            node.children.putIfAbsent(c,new TrieNode());
            node = node.children.get(c);
        }

        node.isWord = true;

        frequency.put(query,frequency.getOrDefault(query,0)+1);
    }

    public List<String> search(String prefix) {

        TrieNode node = root;

        for(char c : prefix.toCharArray()) {
            if(!node.children.containsKey(c))
                return new ArrayList<>();
            node = node.children.get(c);
        }

        List<String> results = new ArrayList<>();
        dfs(node,prefix,results);

        results.sort((a,b)->frequency.get(b)-frequency.get(a));

        return results.subList(0,Math.min(10,results.size()));
    }

    void dfs(TrieNode node,String word,List<String> res){

        if(node.isWord)
            res.add(word);

        for(char c: node.children.keySet()){
            dfs(node.children.get(c),word+c,res);
        }
    }
}

public class PS7 {

    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.insert("java tutorial");
        system.insert("javascript");
        system.insert("java download");
        system.insert("java tutorial");

        List<String> results = system.search("jav");

        for(String s: results)
            System.out.println(s);
    }
}