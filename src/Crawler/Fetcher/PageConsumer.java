package Crawler.Fetcher;

import Crawler.Webpage.Webpage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PageConsumer extends Thread {
    InputStream inputStream;
    Webpage webpage;

    PageConsumer(InputStream inputStream, Webpage webpage) {
        this.inputStream = inputStream;
        this.webpage = webpage;
    }

    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = bufferedReader.readLine();
            while (line != null) {
                webpage.addContentLine(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
