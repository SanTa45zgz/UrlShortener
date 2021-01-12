package urlshortener.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Profile("worker")
@Service
public class CacheService {

    // Save the requests
    private final List<String> tempUrlList;

    // Total urls added
    private long totalCounter = 0;

    // Total urls checked since last counter request
    private long urlsChecked = 0;

    // Total safe urls
    private long urlsSafe = 0;

    public CacheService() {
        this.tempUrlList = new ArrayList<>();
    }

    /**
     * Adds a request to the waiting list
     */
    public void addUrl(String url) {
        totalCounter++;
        urlsChecked++;
        tempUrlList.add(url);
    }

    /**
     * Returns up to 500 requests saved in the waiting list
     *
     * @return List up to 500 urls to send to Google Safe Api
     */
    public List<String> getTempUrlList() {
        List<String> urls;
        // If waiting list has less that 500 requests, all of them are sent
        if (tempUrlList.size() <= 500) {
            urls = new ArrayList<>(tempUrlList);
            tempUrlList.clear();
        } else {
            // If there are more than 500 requests, only the 500 first are sent
            urls = tempUrlList.subList(0, 500);
            tempUrlList.subList(0, 500).clear();
        }
        return urls;
    }

    /**
     * Get total counter of urls received since last check
     *
     * @return counter of urls received
     */
    public long getUrlsReceived() {
        long received = totalCounter;
        totalCounter = 0;
        return received;
    }

    /**
     * Get actual counter of urls checked since last check
     *
     * @return counter of urls checked
     */
    public long getUrlsChecked() {
        long checked = urlsChecked;
        urlsChecked = 0;
        return checked;
    }

    public void addSafeUrls(long newSafeUrls) {
        System.out.println("Guardando nuevas urls seguras ==============> " + newSafeUrls);
        urlsSafe += newSafeUrls;
    }

    /**
     * Get actual counter of safe urls since last check
     *
     * @return counter of safe urls
     */
    public long getUrlsSafe() {
        long safeUrls = urlsSafe;
        urlsSafe = 0;
        return safeUrls;
    }

}
