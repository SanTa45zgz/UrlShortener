
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

    public CacheService() {
        this.tempUrlList = new ArrayList<>();
    }

    /*
        Adds a request to the waiting list
     */
    public boolean addUrl(String url) {
        return tempUrlList.add(url);
    }

    /*
        Returns up to 500 requests saved in the waiting list
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

}
