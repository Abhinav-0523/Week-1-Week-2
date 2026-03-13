import java.util.*;

public class PlagiarismDetector {

    private int n;
    private Map<String, Set<String>> ngramIndex = new HashMap<>();
    private Map<String, List<String>> documentNgrams = new HashMap<>();

    public PlagiarismDetector(int n) {
        this.n = n;
    }

    private List<String> generateNGrams(String text) {
        String[] words = text.toLowerCase().replaceAll("[^a-z0-9 ]", "").split("\\s+");
        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; j++) {
                sb.append(words[i + j]);
                if (j < n - 1) sb.append(" ");
            }
            ngrams.add(sb.toString());
        }

        return ngrams;
    }

    public void addDocument(String docId, String content) {
        List<String> ngrams = generateNGrams(content);
        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {
            ngramIndex.computeIfAbsent(gram, k -> new HashSet<>()).add(docId);
        }
    }

    public void analyzeDocument(String docId, String content) {

        List<String> newDocNgrams = generateNGrams(content);
        Map<String, Integer> matchCounts = new HashMap<>();

        for (String gram : newDocNgrams) {
            if (ngramIndex.containsKey(gram)) {
                for (String existingDoc : ngramIndex.get(gram)) {
                    matchCounts.put(existingDoc, matchCounts.getOrDefault(existingDoc, 0) + 1);
                }
            }
        }

        System.out.println("Extracted " + newDocNgrams.size() + " n-grams");

        for (Map.Entry<String, Integer> entry : matchCounts.entrySet()) {
            String otherDoc = entry.getKey();
            int matches = entry.getValue();
            double similarity = (matches * 100.0) / newDocNgrams.size();

            System.out.println("Found " + matches + " matching n-grams with \"" + otherDoc + "\"");
            System.out.printf("Similarity: %.2f%%\n", similarity);

            if (similarity >= 60) {
                System.out.println("PLAGIARISM DETECTED");
            } else if (similarity >= 10) {
                System.out.println("Suspicious similarity");
            }

            System.out.println();
        }
    }

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector(5);

        String essay1 = "Artificial intelligence is transforming the world of technology and innovation";
        String essay2 = "Artificial intelligence is transforming the world of technology and modern innovation";
        String essay3 = "The history of computers and programming languages is very interesting";

        detector.addDocument("essay_089.txt", essay1);
        detector.addDocument("essay_092.txt", essay2);

        detector.analyzeDocument("essay_123.txt", essay3);
    }
}