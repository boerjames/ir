package TFIDF;

import Crawler.Webpage.Webpage;

import java.io.*;
import java.util.*;

public class DataManager {
    private static final DataManager INSTANCE = new DataManager();
    private File stop_words;
    private File config_file;
    private File tfidf_file;
    private File data_folder;
    private TFIDFUtil.TF_STRATEGY tf_strategy;
    private TFIDFUtil.IDF_STRATEGY idf_strategy;
    private String[] term_array;
    private double[] idf_array;
    private Document[] documents;
    private int num_terms;
    private int num_documents;
    private Lexicon lexicon;

    public static DataManager getInstance() {
        return INSTANCE;
    }

    private DataManager() {
        // set configuration file, this is hard-coded
        config_file = new File("5V93data_parameters.txt");
        lexicon = new Lexicon();

        // setup configuration file scanner and scan it
        Scanner scanner = null;
        try {
            scanner = new Scanner(config_file);
            this.parseFiles(scanner);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void parseFiles(Scanner scanner) {
        // tfidf file
        tfidf_file = new File(TFIDFUtil.configCleanString(scanner.nextLine()));
        Scanner tfidf_scanner = null;
        try {
            tfidf_scanner = new Scanner(tfidf_file);
            this.parseTFIDFFile(tfidf_scanner);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        tfidf_scanner.close();

        // stop words
        stop_words = new File(TFIDFUtil.configCleanString(scanner.nextLine()));
        lexicon.setStopWords(stop_words);
    }

    private void parseTFIDFFile(Scanner scanner) {
        String s = scanner.next();
        tf_strategy = TFIDFUtil.validateTFStrategy(s);
        s = scanner.next();
        idf_strategy = TFIDFUtil.validateIDFStrategy(s);

        s = scanner.next();
        num_terms = Integer.parseInt(s);
        term_array = new String[num_terms];
        idf_array = new double[num_terms];

        for (int i = 0; i < num_terms; i++) {
            s = scanner.next();
            term_array[i] = s;
            lexicon.addWordToDictionary(s);

            s = scanner.next();
            idf_array[i] = Double.parseDouble(s);
        }

        data_folder = new File(scanner.next());

        s = scanner.next();
        num_documents = Integer.parseInt(s);
        documents = new Document[num_documents];

        for (int j = 0; j < num_documents; j++) {
            Document d = new Document(scanner.next());
            for (int i = 0; i < num_terms; i++) {
                Double tf = Double.parseDouble(scanner.next());
                d.addToTFMap(term_array[i], tf);
            }
            documents[j] = d;
        }


    }

    public double sim(Document dj, Webpage q) {
        Double sim = 0.0;
        for (int i = 0; i < term_array.length; i++) {
            double w_ij = dj.getTF(term_array[i]) * idf_array[i];
            double w_iq = TFIDFUtil.getTF(tf_strategy, term_array[i], q) * idf_array[i];
            sim += w_ij * w_iq;
        }

        double sqrt_wij = 0.0;
        for (int i = 0; i < term_array.length; i++) {
            double w_ij = dj.getTF(term_array[i]) * idf_array[i];
            w_ij *= w_ij;
            sqrt_wij += w_ij;
        }
        sqrt_wij = Math.sqrt(sqrt_wij);

        double sqrt_wiq = 0.0;
        for (int i = 0; i < term_array.length; i++) {
            double w_iq = TFIDFUtil.getTF(tf_strategy, term_array[i], q) * idf_array[i];
            w_iq *= w_iq;
            sqrt_wiq += w_iq;
        }
        sqrt_wiq = Math.sqrt(sqrt_wiq);

        sim = sim / (sqrt_wij * sqrt_wiq);

        if (sim.isNaN()) sim = 0.0;
        return sim;
    }

    public Lexicon getLexicon() {
        return lexicon;
    }

    public Document[] getDocuments() {
        return documents;
    }
}
