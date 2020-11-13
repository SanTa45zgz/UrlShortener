package urlshortener.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import urlshortener.domain.Match;
import urlshortener.service.CacheService;
import urlshortener.service.MaliciousService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UrlDealer {

    @Autowired
    CacheService cacheService;

    @Autowired
    MaliciousService maliciousService;

    /*
        This functions is going to check pending requests every 5 seconds
     */
    @Scheduled(fixedRate = 5000)
    public void checkPendingUrlForSafeBrowsing() {
        // Get pending URLs for check-out
        List<Pair<UUID, String>> list = cacheService.getTempUrlList();
        if (!list.isEmpty()) {
            // If list is full, re-run the function to check URLs faster
            if (list.size() == 500) {
                checkPendingUrlForSafeBrowsing();
            }

            // Extract urls from request pairs
            List<String> urls = list.stream().map(Pair::getSecond).collect(Collectors.toList());
            // Send petition to local service
            List<Match> result = maliciousService.checkUrl(urls);

            List<Pair<UUID, Optional<Match>>> matches = new ArrayList<>();
            // Go through request pair list to compare request and SafeBrowsing response
            for (Pair<UUID, String> pair : list) {
                // If response contains the requested url -> BAD NEWS
                if (result.stream().anyMatch(match -> match.getThreat().containsValue(pair.getSecond()))) {
                    Match match = result.stream().filter(m -> m.getThreat().containsValue(pair.getSecond())).findFirst().get();
                    matches.add(Pair.of(pair.getFirst(), Optional.of(match)));
                } else {
                    // Otherwise the url is free of malware
                    matches.add(Pair.of(pair.getFirst(), Optional.empty()));
                }
            }
            // Add the result back to cacheService
            boolean addResult = cacheService.addMatches(matches);
            // Print error if this part fails
            if (addResult) {
                System.out.println("Error adding responses to cacheService");
            }
        }
    }

}
