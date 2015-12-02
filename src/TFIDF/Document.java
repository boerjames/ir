package TFIDF;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by James on 1/27/15.
 */
public class Document {
    protected Map<String, Integer> term_frequencies = new LinkedHashMap<>();
    protected String name;
    private Map<String, Double> tf_map = new LinkedHashMap<>();

    public Document() {
    }

    public Document(String name) {
        this.name = name;
    }

    public Document(File file, Lexicon lexicon) {
        this.name = file.getName();
        this.buildTermFrequencyMap(file, lexicon);
    }

    protected Integer occurrencesOf(String term) {
        if (!term_frequencies.containsKey(term)) {
            return 0;
        } else {
            return term_frequencies.get(term);
        }
    }

    public void addToTFMap(String word, Double tf) {
        tf_map.put(word, tf);
    }

    public Double getTF(String word) {
        return tf_map.get(word);
    }

    public Boolean contains(String term) {
        return term_frequencies.containsKey(term);
    }

    private void buildTermFrequencyMap(File file, Lexicon lexicon) {
        Set<String> stop_words = lexicon.getStopWords();
        Set<String> dictionary = lexicon.getDictionary();
        Scanner scanner = null;

        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (scanner.hasNext()) {
            String next = TFIDFUtil.cleanString(scanner.next());

            String[] split = next.split("\\s+");
            for (int i = 0; i < split.length; i++) {
                if (!stop_words.contains(split[i]) && split[i].length() != 0) {
                    dictionary.add(split[i]);
                    Integer frequency = term_frequencies.get(split[i]);
                    term_frequencies.put(split[i], (frequency == null) ? 1 : frequency + 1);
                }
            }
        }
    }

    private void buildTermFrequencyMap(List<String> stringList, Lexicon lexicon) {
        Set<String> stop_words = lexicon.getStopWords();
        Set<String> dictionary = lexicon.getDictionary();

        for (String s : stringList) {
            String next = TFIDFUtil.cleanString(s);

            String[] split = next.split("\\s+");
            for (int i = 0; i < split.length; i++) {
                if (!stop_words.contains(split[i]) && split[i].length() != 0) {
                    dictionary.add(split[i]);
                    Integer frequency = term_frequencies.get(split[i]);
                    term_frequencies.put(split[i], (frequency == null) ? 1 : frequency + 1);
                }
            }
        }
    }

    public String getTFMap() {
        StringBuilder string = new StringBuilder();
        string.append("{");
        for (String s : term_frequencies.keySet()) {
            string.append(s);
            string.append(":");
            string.append(term_frequencies.get(s));
            string.append(" ");
        }
        string.delete(string.length()-1, string.length());
        string.append("}");
        return string.toString();
    }

    public String toString() {
        return this.name;
    }

}
