package urlshortener.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import urlshortener.domain.GeoLocation;
import urlshortener.domain.RedirectList;

import java.util.ArrayList;
import java.util.List;

public class BrokerClient {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private DirectExchange exchange;

    private static final Logger log = LoggerFactory
            .getLogger(BrokerClient.class);

    public GeoLocation getLocationFromIp(String ip) {
//        log.info("Pidiendo geolocation al server");

        Object response = template.convertSendAndReceive(exchange.getName(), "rpc", "location:" + ip);

        GeoLocation geoLocation = null;
        try {
            geoLocation = GeoLocation.createGeoLocation((String) response);
        } catch (NullPointerException exception) {
            log.info("No se ha podido obtener los clicks: " + exception);
        }
        return geoLocation;
    }

    public long getNumClicksFromServers() {
//        log.info("Pidiendo click al server");

        Object response = template.convertSendAndReceive(exchange.getName(), "rpc", "clicks:dummy");

        long counter = -1;
        try {
            counter = (long) response;
            log.info("Num de clicks => " + counter);
        } catch (NullPointerException exception) {
            log.info("No se ha podido obtener los clicks: " + exception);
        }
        return counter;
    }

    public long countNumUris() {
//        log.info("Pidiendo numUris al server");

        Object response = template.convertSendAndReceive(exchange.getName(), "rpc", "uris:dummy");

        long counter = -1;
        try {
            counter = (long) response;
            log.info("Num de uris => " + counter);
        } catch (NullPointerException exception) {
            log.info("No se ha podido obtener las uris: " + exception);
        }
        return counter;
    }

    public long countUsers() {
//        log.info("Pidiendo numUsers al server");

        Object response = template.convertSendAndReceive(exchange.getName(), "rpc", "users:dummy");

        long counter = -1;
        try {
            counter = (long) response;
            log.info("Num de users => " + counter);
        } catch (NullPointerException exception) {
            log.info("No se ha podido obtener los users: " + exception);
        }
        return counter;
    }

    public List<Pair<String, Long>> getRankingUris() {
//        log.info("Pidiendo rankingUris al server");

        Object response = template.convertSendAndReceive(exchange.getName(), "rpc", "httpRedirects:dummy");

        List<Pair<String, Long>> result = getPairs((String) response);
        log.info("Num de rankingUris => " + result.size());
        return result;
    }

    public List<Pair<String, Long>> getCountryCount() {
//        log.info("Pidiendo numUsers al server");

        Object response = template.convertSendAndReceive(exchange.getName(), "rpc", "geoRedirects:dummy");

        List<Pair<String, Long>> result = getPairs((String) response);
        log.info("Num de geoRedirects => " + result.size());
        return result;
    }

    private List<Pair<String, Long>> getPairs(String response) {
        List<Pair<String, Long>> result = new ArrayList<>();
        try {
            result = RedirectList.createHttpRedirects(response).getRedirects();
        } catch (NullPointerException exception) {
            log.info("No se han podido obtener redirects: " + exception);
        }
        return result;
    }

    public void validateUrl(String url) {
        template.convertAndSend(exchange.getName(),"rpc", "validate:" + url);
    }


}
