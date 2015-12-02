package Crawler.Domain;

import java.util.*;

public class DomainManager {
    private static final DomainManager INSTANCE = new DomainManager();
    private final Set<Domain> domainsInUseThreadSafe;
    private final Set<Domain> domainsInUse;
    private final Map<String,Domain> domainMap;
    private final Map<String,Domain> domainMapThreadSafe;

    private DomainManager() {
        domainsInUse = new HashSet<>();
        domainsInUseThreadSafe = Collections.synchronizedSet(domainsInUse);

        domainMap = new TreeMap<>();
        domainMapThreadSafe = Collections.synchronizedMap(domainMap);
    }

    public static DomainManager getInstance() {
        return INSTANCE;
    }

    public Domain assignDomain(String url) {
        String domainString = Domain.findDomain(url);
        Domain domain;
        if (!domainMapThreadSafe.containsKey(domainString)) {
            domain = new Domain(domainString);
            domainMapThreadSafe.put(domain.toString(), domain);
            return domain;
        } else {
            return domainMapThreadSafe.get(domainString);
        }
    }

    public void setDomainInUse(Domain domain, boolean isInUse) {
        if (isInUse) {
            domainsInUseThreadSafe.add(domain);
        } else {
            if (domainsInUseThreadSafe.contains(domain)) {
                domainsInUseThreadSafe.remove(domain);
            }
        }
    }

    public boolean isDomainInUse(Domain domain) {
        return domainsInUseThreadSafe.contains(domain);
    }

}
