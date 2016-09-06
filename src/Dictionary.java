import javafx.util.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Christian Goldapp
 * @version 1.0
 */
public class Dictionary {
    Map<Integer, List<String>> allWords;
    String message;

    public Dictionary(String filename) {
        long millis = System.currentTimeMillis();
        Pair<Map<Integer, List<String>>, Long> res = readWords(filename);
        millis = System.currentTimeMillis() - millis;
        allWords = res.getKey();
        long count = res.getValue();
        message = String.format("Built Dictionary of %d words in %.3fms.", count, millis / 1000.0);
    }

    private static Pair<Map<Integer, List<String>>, Long> readWords(String filename) {
        Map<Integer, List<String>> allWords = new HashMap<>();
        final long[] n = {0};
        try {
            Files.lines(Paths.get(filename)).forEach(s -> {
                addWord(allWords, s.toLowerCase());
                n[0]++;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Pair<>(allWords, n[0]);
    }

    private static void addWord(Map<Integer, List<String>> words, String s) {
        int l = s.length();
        if (!words.containsKey(l)) {
            words.put(l, new ArrayList<>());
        }
        words.get(l).add(s);
    }

    private List<String> findFittingWords(int length, String letters) {
        List<String> words = Collections.synchronizedList(new ArrayList<>());
        for (String s : allWords.get(length)) {
            if (canBeAssembledFrom(letters, s)) {
                words.add(s);
            }
        }
        return words;
    }

    private static boolean canBeAssembledFrom(String origin, String target) {
        origin = origin.toLowerCase();
        target = target.toLowerCase();
        List<Character> chars = new ArrayList<>(origin.length());
        for (char c : origin.toCharArray()) chars.add(c);
        for (char x : target.toCharArray()) {
            if (!chars.contains(x)) {
                return false;
            } else {
                chars.remove((Character) x);
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return message;
    }

    public static void main(String[] args) {
        Dictionary d = new Dictionary("german.dic");
        System.out.println(d);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("exit")) {
                break;
            }
            Scanner linescan = new Scanner(line);
            int length = linescan.nextInt();
            String letters = linescan.nextLine().trim();
            System.out.println(d.findFittingWords(length, letters));
        }
    }

}
