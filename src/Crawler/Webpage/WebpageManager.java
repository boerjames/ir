package Crawler.Webpage;

import java.util.*;

public class WebpageManager {
    private static final WebpageManager INSTANCE = new WebpageManager();
    private final Set<Webpage> webpagesThreadSafe;
    private final Set<Webpage> webpages;
    private final Set<Webpage> webpagesToCrawl;
    private final Set<Webpage> webpagesToCrawlThreadSafe;
    private final Set<Webpage> webpagesToCrawlTemp;
    private final Set<Webpage> webpagesToCrawlTempThreadSafe;

    private WebpageManager() {
        webpages = new HashSet<>();
        webpagesThreadSafe = Collections.synchronizedSet(webpages);

        webpagesToCrawl = new LinkedHashSet<>();
        webpagesToCrawlThreadSafe = Collections.synchronizedSet(webpagesToCrawl);

        webpagesToCrawlTemp = new HashSet<>();
        webpagesToCrawlTempThreadSafe = Collections.synchronizedSet(webpagesToCrawlTemp);
    }

    public static WebpageManager getInstance() {
        return INSTANCE;
    }

    public Set<Webpage> getWebpagesToCrawl() {
        return webpagesToCrawlThreadSafe;
    }

    public void addWebpageToCrawl(Webpage w) {
        webpagesToCrawlTempThreadSafe.add(w);
    }

    public void synchronizeWebpagesToCrawl() {
        synchronized (webpagesToCrawlTempThreadSafe) {
            webpagesToCrawlThreadSafe.addAll(webpagesToCrawlTempThreadSafe);
            webpagesToCrawlTempThreadSafe.clear();
        }
    }

}
