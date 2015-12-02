package TFIDF;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

/**
 * Created by James on 1/29/15.
 */
public class TFIDFUtil {

    public enum TF_STRATEGY {
        RAW('r'), LOG('l'), BINARY('b');

        private char representation;
        TF_STRATEGY(char c) {
            representation = c;
        }
        @Override
        public String toString() {
            return String.valueOf(representation);
        }
    }

    public enum IDF_STRATEGY {
        INVERSE('i'), SMOOTHED('s'), PROBABILISTIC('p');

        private char representation;
        IDF_STRATEGY(char c) {
             representation = c;
        }

        @Override
        public String toString() {
            return String.valueOf(representation);
        }
    }

    public static String cleanString(String s) {
        return s.trim().replace("'","").replaceAll("[^A-Za-z0-9]", " ");
    }

    public static String configCleanString(String s) {
        String str = s.replaceAll("\\s+", "");

        if (str.contains("#")) {
            return str.substring(0, str.indexOf('#'));
        } else {
            return str;
        }
    }

    public static String cleanQueryString(String s) {
        String str;
        if (s.contains("#")) {
            str = s.substring(0, s.indexOf('#'));
        } else {
            str = s;
        }
        return str.trim().replaceAll("[']", "").replaceAll("[^A-Za-z0-9]", " ");
    }

    public static File[] getListOfFiles(File data_folder, String file_type) {
        File[] files;
        final String f_type = file_type;
        if (file_type.charAt(0) != '$') {
            files = data_folder.listFiles(new FilenameFilter() {
                public boolean accept(File data_folder, String name) {
                    return name.toLowerCase().endsWith(f_type);
                }
            });
        } else {
            files = data_folder.listFiles();
        }
        return files;
    }

    public static TF_STRATEGY validateTFStrategy(String string) {
        Character character = string.toLowerCase().charAt(0);
        TF_STRATEGY tf_strategy;
        switch (character) {
            case 'r':
                tf_strategy = TF_STRATEGY.RAW;
                break;
            case 'l':
                tf_strategy = TF_STRATEGY.LOG;
                break;
            case 'b':
                tf_strategy = TF_STRATEGY.BINARY;
                break;
            default:
                tf_strategy = TF_STRATEGY.LOG;
                System.err.println("Invalid TF strategy. Closing program.");
                System.exit(1);
                break;
        }
        return tf_strategy;
    }

    public static IDF_STRATEGY validateIDFStrategy(String string) {
        Character character = string.toLowerCase().charAt(0);
        IDF_STRATEGY idf_strategy;
        switch (character) {
            case 'i':
                idf_strategy = IDF_STRATEGY.INVERSE;
                break;
            case 's':
                idf_strategy = IDF_STRATEGY.SMOOTHED;
                break;
            case 'p':
                idf_strategy = IDF_STRATEGY.PROBABILISTIC;
                break;
            default:
                idf_strategy = IDF_STRATEGY.SMOOTHED;
                System.err.println("Invalid IDF strategy. Closing program.");
                System.exit(1);
                break;
        }
        return idf_strategy;
    }

    public static Double getTF(TF_STRATEGY tf_strategy, String term, Document document) {
        double tf = 0.0;
        switch (tf_strategy) {
            case BINARY:
                if (document.contains(term))
                    tf = 1.0;
                break;
            case RAW:
                tf = document.occurrencesOf(term);
                break;
            case LOG:
//                if (document.occurrencesOf(term) == 0) {
//                    tf = 0.0;
//                } else {
//                    tf = 1.0 + Math.log(document.occurrencesOf(term));
//                }
                tf = Math.max(0.0, 1.0 + Math.log(document.occurrencesOf(term)));
                break;
            default:
                break;
        }
        return tf;
    }

    public static Double getIDF(IDF_STRATEGY idf_strategy, String term, Set<Document> documents) {
        double N = documents.size();
        double n_i = 0.0;
        double idf = 0.0;

        for (Document d_j : documents) {
            if (d_j.contains(term)) {
                n_i += 1.0;
            }
        }

        switch (idf_strategy) {
            case INVERSE:
//                if (n_i == 0.0) {
//                    idf = 0.0;
//                } else {
//                    idf = Math.log(N / n_i);
//                }
                idf = Math.max(0.0, Math.log(N / n_i));
                break;
            case SMOOTHED:
//                if (n_i == 0.0) {
//                    idf = 0.0;
//                } else {
//                    idf = Math.log(1 + (N / n_i));
//                }
                idf = Math.max(0.0, Math.log(1 + (N / n_i)));
                break;
            case PROBABILISTIC:
//                if (n_i == 0.0) {
//                    idf = 0.0;
//                } else {
//                    idf = Math.log((N - n_i) / n_i);
//                    if (idf < 0) {
//                        idf = 0.0;
//                    }
//                }
                idf = Math.max(0.0, Math.log((N - n_i) / n_i));
                break;
            default:
                break;
        }

        return idf;
    }

    // open source generic sort treemap by values
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map ) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 ) {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }
}
