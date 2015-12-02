package TFIDF;

import java.io.*;
import java.util.*;

/**
 * Created by James on 1/26/15.
 */
public class TFIDF {
    private File data_folder;
    private File stop_words;
    private File config_file;
    private File tfidf_file;
    private Set<File> ignored_documents;
    private String file_type;
    private File[] files;
    private Lexicon lexicon;
    private Set<Document> documents;
    private TFIDFUtil.TF_STRATEGY tf_strategy;
    private TFIDFUtil.IDF_STRATEGY idf_strategy;

    public TFIDF(File config) {
        // set configuration file
        config_file = config;

        // setup configuration file scanner and scan it
        Scanner scanner = null;
        try {
            scanner = new Scanner(config_file);
            this.parseAndBuild(scanner);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        scanner.close();

        // write vector file
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(tfidf_file, false));
            this.writeVectorFile(writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done.");
    }

    private void writeVectorFile(BufferedWriter writer) throws IOException {
        System.out.println("Writing TFIDF vector file...");
        // specifies how TF and IDF are calculated
        writer.write(tf_strategy + " " + idf_strategy);
        writer.newLine();

        // number of terms
        int num_terms = lexicon.getNumberOfTerms();
        writer.write(String.valueOf(num_terms));
        writer.newLine();

        // every term in the order that it will appear for each document with its idf
        // keep track for later
        String[] term_array = new String[num_terms];
        double[] idf_array = new double[num_terms];
        int index = 0;
        for (String term : lexicon.getDictionary()) {
            double idf = TFIDFUtil.getIDF(idf_strategy, term, documents);
            writer.write(term + " " + idf);
            writer.newLine();
            term_array[index] = term;
            idf_array[index++] = idf;
        }

        // data folder where the text collection is
        writer.write(data_folder.toString());
        writer.newLine();

        // number of text files
        writer.write(String.valueOf(documents.size()));
        writer.newLine();

        // td-idf vector for each file
        for (Document d_j : documents) {
            writer.write(d_j.toString());
            writer.newLine();

            int counter = 0;
            for (int i = 0; i < term_array.length; i++) {
                String t_i = term_array[i];
                double idf = idf_array[i];
                double tf = TFIDFUtil.getTF(tf_strategy, t_i, d_j);
                double tf_idf = tf * idf;

                // 10 entries per row
                if (counter == 10) {
                    counter = 0;
                    writer.newLine();
                }

                writer.write(tf_idf + " ");
                counter++;
            }
            writer.newLine();
        }
    }

    private void parseAndBuild(Scanner scanner) {
        // parse settings from configuration file
        System.out.println("Parsing config file...");
        data_folder = new File(TFIDFUtil.configCleanString(scanner.nextLine()));
        file_type = TFIDFUtil.configCleanString(scanner.nextLine());
        stop_words = new File(data_folder.getName() + "/" + TFIDFUtil.configCleanString(scanner.nextLine()));
        tf_strategy = TFIDFUtil.validateTFStrategy(TFIDFUtil.configCleanString(scanner.nextLine()));
        idf_strategy = TFIDFUtil.validateIDFStrategy(TFIDFUtil.configCleanString(scanner.nextLine()));
        tfidf_file = new File(data_folder.getName() + "/" + TFIDFUtil.configCleanString(scanner.nextLine()));

        // file lists
        files = TFIDFUtil.getListOfFiles(data_folder, file_type);
        ignored_documents = new HashSet<>();
        documents = new HashSet<>();

        // build lexicon (word set and stop words)
        lexicon = new Lexicon(stop_words);
        ignored_documents.add(stop_words);
        ignored_documents.add(config_file);
        ignored_documents.add(tfidf_file);

        // build each document
        for (File file : files) {
            System.out.println("Building TF vector for \"" + file.getName() + "\"...");
            if (!ignored_documents.contains(file)) {
                Document document = new Document(file, lexicon);
                documents.add(document);
            }
        }

        scanner.close();
    }

}
