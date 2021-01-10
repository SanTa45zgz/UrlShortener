package urlshortener.broker;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import urlshortener.domain.CheckedMatches;
import urlshortener.service.ShortURLService;

@Profile("webapp")
public class BrokerSafeness {

    private final ShortURLService shortURLService;

    public BrokerSafeness(ShortURLService shortURLService) {
        this.shortURLService = shortURLService;
    }

    @RabbitListener(queues = "url.shortener.rpc.responses.responses")
    public void consumeResponses(String message) {
        // Build message object
        CheckedMatches checkedMatches = CheckedMatches.createCheckedMatches(message);
        // Fetch every url and check if there is any match
        for (String url : checkedMatches.getUrls()) {
            // Update all url database entries
            shortURLService.updateShortURLSafeness(url, !checkedMatches.getMatches().contains(url));
        }
    }
}
