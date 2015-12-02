package TFIDF;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Lexicon {
    private Set<String> dictionary = new TreeSet<>();
    private Set<String> stop_words = new HashSet<>();

    public Lexicon() {

    }

    public Lexicon(File stop_file) {
        this.addStringsFromFileToSet(stop_file, this.stop_words);
    }

    public Set<String> getDictionary() {
        return this.dictionary;
    }

    public int getNumberOfTerms() {
        return this.dictionary.size();
    }

    public String toString() {
        StringBuilder string = new StringBuilder("{");
        for (String word : dictionary) {
            string.append(word);
            string.append(", ");
        }
        string.delete(string.length()-2, string.length());
        string.append("}");
        return string.toString();
    }

    public Set<String> getStopWords() {
        return this.stop_words;
    }

    public void setStopWords(File file) {
        this.addStringsFromFileToSet(file, stop_words);
    }

    public void addWordToDictionary(String word) {
        dictionary.add(word);
    }

    private void addStringsFromFileToSet(File file, Set<String> set) {
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
                if (split[i].length() != 0) {
                    set.add(split[i]);
                }
            }
        }
        set.remove("");
    }
}
