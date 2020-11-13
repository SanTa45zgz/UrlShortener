package urlshortener.service;

import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import urlshortener.domain.Match;

import java.util.*;
import java.util.concurrent.Future;

@Service
public class CacheService {

    // Save the requests
    private final List<Pair<UUID, String>> tempUrlList;

    // SAve the responses
    private final List<Pair<UUID, Optional<Match>>> matchList;

    public CacheService() {
        this.tempUrlList = new ArrayList<>();
        this.matchList = new ArrayList<>();
    }

    /*
        Adds a request to the waiting list
     */
    public boolean addUrl(UUID uuid, String url) {
        return tempUrlList.add(Pair.of(uuid, url));
    }

    /*
        Returns up to 500 requests saved in the waiting list
     */
    public List<Pair<UUID, String>> getTempUrlList() {
        List<Pair<UUID, String>> urls;
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

    /*
        Adds a list of response to response list
     */
    public boolean addMatches(List<Pair<UUID, Optional<Match>>> matches) {
        return matchList.addAll(matches);
    }

    /*
        Async function with waiting purpose till Safe Browsing API responses to client request
     */
    @Async
    public Future<Optional<Match>> waitResponse(UUID uuid) throws InterruptedException {
        System.out.println("Llamo a esperar (" + uuid.toString() + ")");

        // Thread goes sleep if the response haven't come yet
        while (matchList.stream().noneMatch(pair -> pair.getFirst() == uuid)) {
            int waitingList = matchList.size();
            int sleepFor = 1000 - waitingList;
            System.out.println("Sleep for  (" + uuid.toString() + ")");
            Thread.sleep(sleepFor);
        }

        // Finds the waking-up reason
        Optional<Pair<UUID, Optional<Match>>> pair = matchList.stream().filter(Pair -> Pair.getFirst() == uuid).findFirst();
        Optional<Match> match = Optional.empty();
        // If response means malware, the response is added
        if (pair.isPresent() && pair.get().getSecond().isPresent()) {
            match = pair.get().getSecond();
        }
        return new AsyncResult<>(match);
    }
}
