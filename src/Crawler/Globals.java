package Crawler;

import Crawler.Domain.DomainManager;
import Crawler.Fetcher.QueueProducer;
import Crawler.Webpage.WebpageManager;
import TFIDF.DataManager;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class Globals {
    public final static DataManager theDataManager = DataManager.getInstance();
    public final static WebpageManager theWebpageManager = WebpageManager.getInstance();
    public final static DomainManager theDomainManager = DomainManager.getInstance();
    public final static QueueProducer theQueueProducer = QueueProducer.getInstance();
    public static File theWebpageFolder = null;
    public static Double thePageSimilarityThreshold = null;
    public static Double thePagePercentDocumentsRequired = null;
    public static Double theLinksSimilarityThreshold = null;
    public static Double theLinksPercentDocumentsRequired = null;
    public static Integer theNumPages = null;
    public static ThreadPoolExecutor theThreadPool = null;
    public volatile static Integer theNumSavedPages = 0;
    public final static BlockingQueue<Runnable> theQueue = new LinkedBlockingQueue<>();


    public static void setTheWebpageFolder(File in) {
        if (theWebpageFolder == null) theWebpageFolder = in;
    }

    public static void setThePageSimilarityThreshold(double in) {
        if (thePageSimilarityThreshold == null) thePageSimilarityThreshold = in;
    }

    public static void setThePagePercentDocumentsRequired(double in) {
        if (thePagePercentDocumentsRequired == null) thePagePercentDocumentsRequired = in;
    }

    public static void setTheLinksSimilarityThreshold(double in) {
        if (theLinksSimilarityThreshold == null) theLinksSimilarityThreshold = in;
    }

    public static void setTheLinksPercentDocumentsRequired(double in) {
        if (theLinksPercentDocumentsRequired == null) theLinksPercentDocumentsRequired = in;
    }

    public static void setTheNumPages(int in) {
        if (theNumPages == null) theNumPages = in;
    }

    public synchronized static void incrementNumSavedPages() {
        theNumSavedPages++;
    }

    public static void setTheThreadPool(ThreadPoolExecutor tpe) {
        if (theThreadPool == null) theThreadPool = tpe;
    }
}
