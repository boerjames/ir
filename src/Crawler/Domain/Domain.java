package Crawler.Domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class Domain {
    private final String domainString;
    private final Set<String> disallowed;
    private Long lastAccess = 0L;

    public Domain(String domainString) {
        this.domainString = domainString;
        this.disallowed = new HashSet<>();
        this.setDisallowed();
    }

    public String toString() {
        return this.domainString;
    }

    // i pulled this from stack overflow because
    // my version wasn't working
    public static String findDomain(String url) {
        if (url == null || url.length() == 0)
            return "";

        int doubleslash = url.indexOf("//");
        if (doubleslash == -1)
            doubleslash = 0;
        else
            doubleslash += 2;

        int end = url.indexOf('/', doubleslash);
        end = end >= 0 ? end : url.length();

        int port = url.indexOf(':', doubleslash);
        end = (port > 0 && port < end) ? port : end;

        String ret = url.substring(doubleslash, end);
        return url.substring(doubleslash, end);
    }

    public boolean isDisallowed(String url) {
        boolean isExcluded = false;
        for (String s : this.disallowed) {
            if (url.contains(s)) {
                isExcluded = true;
                break;
            }
        }
        return isExcluded;
    }

    private void setDisallowed() {
        URL url;
        HttpURLConnection connection = null;
        String robotsString = "http://" + this.domainString + "/robots.txt";
        try {
            url = new URL(robotsString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;

            // intentionally very cautious with disallowed pages, it's also easier
            // if any robot is disallowed from a given page, disallow this robot
            // from that page also
            while ((line = reader.readLine()) != null) {
                if (!line.contains("#") && line.contains("Disallow")) {
                    String[] strings = line.split(" ");
                    if (strings.length == 2) {
                        this.disallowed.add(strings[1]);
                    }
                }
            }
        } catch (MalformedURLException e) {
            return;
        } catch (IOException e) {
            return;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public Long getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Long lastAccess) {
        this.lastAccess = lastAccess;
    }
}
