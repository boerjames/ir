package Crawler.Webpage;

import Crawler.Globals;
import Crawler.Domain.Domain;
import TFIDF.Document;
import TFIDF.TFIDFUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Webpage extends Document {
    private final List<String> contentLines;
    private final String url;
    private final Domain domain;

    public Webpage(String url) {
        this.url = url;
        this.domain = Globals.theDomainManager.assignDomain(url);
        this.contentLines = new ArrayList<>();
    }

    public void buildTermFrequencies() {
        Set<String> stop_words = Globals.theDataManager.getLexicon().getStopWords();
        for (String s : contentLines) {
            String string = TFIDFUtil.cleanString(s);
            String[] split = string.split("\\s+");
            for (int i = 0; i < split.length; i++) {
                split[i] = split[i].toLowerCase();
                if (!stop_words.contains(split[i]) && split[i].length() != 0) {
                    Integer frequency = term_frequencies.get(split[i]);
                    term_frequencies.put(split[i], (frequency == null) ? 1 : frequency + 1);
                }
            }
        }
    }

    public Double[] percentDocumentsWithinSimilarities(double sim1, double sim2) {
        double res1 = 0.0;
        double res2 = 0.0;
        Document[] documents = Globals.theDataManager.getDocuments();
        for (Document d : documents) {
            double similarity = Globals.theDataManager.sim(d, this);
            if (similarity >= sim1) res1++;
            if (similarity >= sim2) res2++;
        }

        res1 /= documents.length;
        res2 /= documents.length;

        Double[] ret = {res1, res2};
        return ret;
    }

    public String getURL() {
        return this.url;
    }

    public String generateName() {
        String fixedUrl = this.url.replaceAll("[^a-zA-Z0-9]", "");
        String str = String.valueOf(System.currentTimeMillis()) + "-" + fixedUrl + ".txt";
        return str;
    }

    public Domain getDomain() {
        return this.domain;
    }

    public void addContentLine(String string) {
        this.contentLines.add(string);
    }

    public List<String> getContent() {
        return this.contentLines;
    }

    public boolean isValid() {
        boolean isValid;
        if (this.requestOK() && this.domainOK()) {
            isValid = true;
        } else {
            isValid = false;
        }

        System.out.println(this.url + " is valid? " + isValid);
        return isValid;
    }

    private boolean requestOK() {
        boolean isValid = false;
        HttpURLConnection connection = null;
        URL url = null;
        try {
            url = new URL(this.url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD"); // less resource intensive request
            int code = connection.getResponseCode();

            //1xx informational
            //2xx success
            //3xx redirection
            //4xx client error
            //5xx server error
            if (code < 400) isValid = true;

        } catch (MalformedURLException e) {
            System.err.println("Bad URL: " + url);
        } catch (IOException e) {
            System.err.println("Bad URL: " + url);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return isValid;
    }

    private boolean domainOK() {
        if (this.url.contains(".edu")) {
            return true;
        } else {
            return false;
        }
    }

}
