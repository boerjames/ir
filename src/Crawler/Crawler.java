package Crawler;

import Crawler.Fetcher.QueueProducer;
import Crawler.Webpage.Webpage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Crawler {
    private final Set<Webpage> seedWebpages = new HashSet<>();
    private final File configFile;
    private final String folderOptionString;
    private final int numThreads;
//    private final int numPages;

    public Crawler(String configFileString) {
        // parse inside the constructor because I want everything to be final
        System.out.println("Setting up crawler...");
        this.configFile = new File(configFileString);

        // set up scanner
        Scanner scanner = null;
        try {
            scanner = new Scanner(this.configFile);
        } catch (FileNotFoundException e) {
            System.err.println("Crawler config file not found!");
            e.printStackTrace();
        }

        // number of pages to be stored
        String line = scanner.nextLine();
        line = this.cleanConfigString(line);
        Globals.setTheNumPages(Integer.parseInt(line));
        System.out.println("Number of pages to save: " + Globals.theNumPages);
        if (Globals.theNumPages <= 0) {
            System.err.println("Number of pages should be positive.");
            System.exit(1);
        }

        // website folder
        line = scanner.nextLine();
        line = this.cleanConfigString(line);
        String[] strings = line.split(" ");
        Globals.setTheWebpageFolder(new File(strings[0]));
        folderOptionString = strings[1];
        System.out.println("Folder to store saved websites: " + Globals.theWebpageFolder.getAbsolutePath());

        // website folder erase option
        System.out.print("Folder will be cleared first? ");
        if (folderOptionString.equals("O")) {
            this.eraseFilesFromFolder(Globals.theWebpageFolder);
            System.out.println("True");
        } else {
            System.out.println("False");
        }

        // num threads
        line = scanner.nextLine();
        line = this.cleanConfigString(line);
        numThreads = Integer.parseInt(line);
        System.out.println("Number of fetching threads to use: " + numThreads);
        if (numThreads <= 0) {
            System.err.println("Number of fetching threads should be positive.");
            System.exit(1);
        }

        // 3 seed websites
        for (int i = 0; i < 3; i++) {
            line = scanner.nextLine();
            line = this.cleanConfigString(line);
            Webpage w = new Webpage(line);
            seedWebpages.add(w);
            System.out.println("Seed webpage: " + w.getDomain().toString());
        }

        // threshold values for pages
        line = scanner.nextLine();
        line = this.cleanConfigString(line);
        strings = line.split(" ");
        Globals.setThePageSimilarityThreshold(Double.parseDouble(strings[0]));
        System.out.println("Page similarity threshold: " + Globals.thePageSimilarityThreshold);
        if (Globals.thePageSimilarityThreshold < 0.0 || Globals.thePageSimilarityThreshold > 1.0) {
            System.err.println("Page similarity threshold should be between 0.0 and 1.0");
            System.exit(1);
        }
        Globals.setThePagePercentDocumentsRequired(Double.parseDouble(strings[1]));
        System.out.println("Page percent docs required: " + Globals.thePagePercentDocumentsRequired);
        if (Globals.thePagePercentDocumentsRequired < 0.0 || Globals.thePagePercentDocumentsRequired > 1.0) {
            System.err.println("Page percent docs required should be between 0.0 and 1.0");
            System.exit(1);
        }

        // threshold values for links
        line = scanner.nextLine();
        line = this.cleanConfigString(line);
        strings = line.split(" ");
        Globals.setTheLinksSimilarityThreshold(Double.parseDouble(strings[0]));
        System.out.println("Links similarity threshold: " + Globals.theLinksSimilarityThreshold);
        if (Globals.theLinksSimilarityThreshold < 0.0 || Globals.theLinksSimilarityThreshold > 1.0) {
            System.err.println("Links similarity threshold should be between 0.0 and 1.0");
            System.exit(1);
        }
        Globals.setTheLinksPercentDocumentsRequired(Double.parseDouble(strings[1]));
        System.out.println("Links percent docs required: " + Globals.theLinksPercentDocumentsRequired);
        if (Globals.theLinksPercentDocumentsRequired < 0.0 || Globals.theLinksPercentDocumentsRequired > 1.0) {
            System.err.println("Links percents docs required should be between 0.0 and 1.0");
            System.exit(1);
        }
        scanner.close();

        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(numThreads, numThreads, Long.MAX_VALUE, TimeUnit.NANOSECONDS, Globals.theQueue);
        Globals.setTheThreadPool(threadPool);
        System.out.println("Setting up crawler completed.\n");
    }

    public void crawl() {
        System.out.println("Starting crawler...");

        // start crawling the seed webpages
        Globals.theThreadPool.prestartAllCoreThreads();
        for (Webpage webpage : seedWebpages) {
            Globals.theWebpageManager.addWebpageToCrawl(webpage);
        }


        // set up a producer and keep feeding into
        QueueProducer queueProducer = QueueProducer.getInstance();
        Thread queueProducerThread = new Thread(queueProducer);
        queueProducerThread.start();
        try {
            queueProducerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Globals.theThreadPool.shutdown();
        System.out.println("Background threads finishing up...");
    }

    private String cleanConfigString(String s) {
        if (s.contains("#")) {
            return s.substring(0, s.indexOf('#')).trim();
        } else {
            return s;
        }
    }

    private void eraseFilesFromFolder(File folder) {
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                file.delete();
            }
        }
    }
}
