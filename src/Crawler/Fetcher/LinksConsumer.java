package Crawler.Fetcher;

import Crawler.Domain.Domain;
import Crawler.Globals;
import Crawler.Webpage.Webpage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LinksConsumer extends Thread {
    InputStream inputStream;

    LinksConsumer(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ( (line = bufferedReader.readLine()) != null) {
                String[] strings = line.split(" ");
                for (String s : strings) {
                    if (Domain.findDomain(s).contains(".edu") && !s.contains("https")) {
                        Webpage w = new Webpage(s);
                        Globals.theWebpageManager.addWebpageToCrawl(w);
                    }
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
