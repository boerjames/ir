package Crawler.Fetcher;

import Crawler.Globals;
import Crawler.Webpage.Webpage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.concurrent.TimeUnit;

public class FetcherRunnable implements Runnable {
    protected final static String lynx = "lynx";
    protected final static String dump = "-dump";
    protected final static String page = "-nolist";
    protected final static String links = "-listonly";
    private final Webpage webpage;

    public FetcherRunnable(Webpage webpage) {
        this.webpage = webpage;
    }

    @Override
    public void run() {
        ProcessBuilder builder = new ProcessBuilder(lynx, dump, page, webpage.getURL());
        Process process;
        Thread thread;
        BufferedWriter bw = null;

        // build webpage contents
        try {
            process = builder.start();
            thread = new PageConsumer(process.getInputStream(), webpage);
            thread.start();
            // timeout the process after a given time
            if (!process.waitFor(5, TimeUnit.SECONDS)) {
                System.err.println("Intentionally killed for " + webpage.getURL());
                process.destroy();
            }

            thread.join();
            webpage.buildTermFrequencies();
            Double[] percentDocs = webpage.percentDocumentsWithinSimilarities(Globals.thePageSimilarityThreshold, Globals.theLinksSimilarityThreshold);

            // if enough documents, write to file
            if (percentDocs[0] >= Globals.thePagePercentDocumentsRequired) {
                String filename = webpage.generateName();
                bw = new BufferedWriter(new FileWriter(Globals.theWebpageFolder.getAbsolutePath() + "/" + filename));
                bw.write(webpage.getURL()+"\n");
                for (String s : webpage.getContent()) {
                    bw.write(s + "\n");
                }
                bw.flush();
                bw.close();
                Globals.incrementNumSavedPages();
                System.out.println(Globals.theNumSavedPages + ": " + webpage.getURL());
            }

            // if enough documents, crawl links
            if (percentDocs[1] >= Globals.theLinksPercentDocumentsRequired) {
                builder = new ProcessBuilder(lynx, dump, links, webpage.getURL());
                process = builder.start();
                thread = new LinksConsumer(process.getInputStream());
                thread.start();
                // timeout the process after a given time
                if (!process.waitFor(5, TimeUnit.SECONDS)) {
                    System.err.println("Intentionally killed for " + webpage.getURL());
                    process.destroy();
                }
                thread.join();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Globals.theDomainManager.setDomainInUse(webpage.getDomain(),false);
        }


    }
}
