package urlshortener.tasks;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import urlshortener.domain.CheckedMatches;
import urlshortener.domain.Match;
import urlshortener.service.CacheService;
import urlshortener.service.MaliciousService;

import java.util.ArrayList;
import java.util.List;

@Profile("worker")
@Component
public class UrlDealer {

    private final RabbitTemplate template;

    private final DirectExchange exchangeResponses;

    private final CacheService cacheService;

    private final MaliciousService maliciousService;

    public UrlDealer(RabbitTemplate template, DirectExchange exchangeResponses, CacheService cacheService, MaliciousService maliciousService) {
        this.template = template;
        this.exchangeResponses = exchangeResponses;
        this.cacheService = cacheService;
        this.maliciousService = maliciousService;
    }

    /*
        This functions is going to check pending requests every second
     */
    @Scheduled(fixedRate = 1000)
    public void checkPendingUrlForSafeBrowsing() {
        // Get pending URLs for check-out
        List<String> urls = cacheService.getTempUrlList();
        if (!urls.isEmpty()) {

            // Extract urls from request pairs
            // Send petition to local service
            List<Match> result = maliciousService.checkUrl(urls);

            List<String> matches = new ArrayList<>();
            // Go through request pair list to compare request and SafeBrowsing response
            for (String url : urls) {
                // If response contains the requested url -> BAD NEWS
                if (result.stream().anyMatch(match -> match.getThreat().containsValue(url))) {
                    matches.add(url);
                }
            }
            // Save new safe urls count
            cacheService.addSafeUrls(urls.size() - matches.size());
            // Create response object
            CheckedMatches checkedMatches = new CheckedMatches(urls, matches);
            // Add the result back to cacheService
            template.convertAndSend(exchangeResponses.getName(), "rpc-responses", checkedMatches.toString());
        }
    }

}
