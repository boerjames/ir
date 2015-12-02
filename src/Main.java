import Crawler.*;

public class Main {

    /**
     * The main method the gets everything going.
     * A few notes:
     *
     * 1. I created a new parameters file called 5V93data_parameters.txt
     * that the TFIDF document builder uses rather than use the 5V93query.txt
     * file as specified in the homework. Shouldn't make much difference
     * From hwk1, along with a new class "DataManager". This file has been
     * hardcoded in the making of that class for convenience.
     *
     * 2. Everything is pretty straightforward. A bunch of fetcher threads and
     * consumers for the page contents and links and a producer for the fetcher
     * thread queue. The Globals class is a singleton and contains all of these
     * other classes as singletons.
     *
     * I find it nicer to read how a program works in one place,
     * rather than go through each class and read fragmented comments,
     * so, the architecture is as follows:
     * Crawler
     *  -reads in crawler_parameters.txt and adds the seed websites to TheWebpageManager
     *  -starts up the QueueProducer and sets the ThreadPool to terminate when all fetching is done
     *
     * TheWebpageManager
     *  -keeps track of websites that still need to be visited
     *
     * QueueProducer
     *  -runs through the websites TheWebpageManager has and looks for a website that can be fetched
     *  -when such a website is found it is added to the queue (for the ThreadPool)
     *
     * TheDomainManager
     *  -keep track of the domains that have been visited
     *  -builds robot exclusions for each domain
     *  -keeps track of the last time a domain has been visited
     *
     * FetcherRunnable
     *  -does the actual fetching using lynx and directs the contents to a consumer
     *  -each webpage fetch will get its own thread for this
     *
     * PageConsumer and LinksConsumer
     *  -threads that read from the FetcherRunnable
     *  PageConsumer builds the webpage contents
     *  LinksConsumer sends the links to TheWebpageManager
     *
     * @param args the crawler parameter file
     */
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Crawler c = new Crawler(args[0]);
        c.crawl();

        System.out.println("Crawler done in " + (System.currentTimeMillis() - startTime)/1000.0 + " seconds.");
    }
}
