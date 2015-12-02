package Crawler.Fetcher;

import Crawler.Domain.Domain;
import Crawler.Globals;
import Crawler.Webpage.Webpage;

import java.util.Iterator;

public class QueueProducer implements Runnable {
    private static final QueueProducer INSTANCE = new QueueProducer();

    public static QueueProducer getInstance() {
        return INSTANCE;
    }

    private QueueProducer() {
    }

    @Override
    public void run() {
        while (Globals.theNumSavedPages < Globals.theNumPages) {
            Webpage webpage;
            boolean crawled = false;
            Iterator<Webpage> it = Globals.theWebpageManager.getWebpagesToCrawl().iterator();
            while (it.hasNext() && !crawled) {
                webpage = it.next();
                Domain domain = webpage.getDomain();

                // if domain is not in use
                if (!Globals.theDomainManager.isDomainInUse(domain)) {
                    // if domain hasn't been visited in 15 seconds
                    if (domain.getLastAccess() + 15000 < System.currentTimeMillis()) {
                        // if domain is allowed
                        if (!domain.isDisallowed(webpage.getURL())) {
                            // crawl this webpage and break out of the while loop
                            Globals.theDomainManager.setDomainInUse(domain, true);
                            domain.setLastAccess(System.currentTimeMillis());
                            Runnable runnable = new FetcherRunnable(webpage);
                            crawled = true;
                            try {
                                Globals.theThreadPool.getQueue().put(runnable);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                // don't crawl this page again
                if (crawled) it.remove();
            }
            // synchronize with the new pages TheWebpageManager has
            Globals.theWebpageManager.synchronizeWebpagesToCrawl();
        }

    }
}
