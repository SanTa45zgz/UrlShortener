package urlshortener.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import urlshortener.domain.GeoLocation;

public class BrokerClient {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private DirectExchange exchange;

    private static final Logger log = LoggerFactory
            .getLogger(BrokerClient.class);

    public long getNumClicksFromServers() {

        log.info("Pidiendo click al server");

        Object response = template.convertSendAndReceive(exchange.getName(),"rpc", "clicks:client");

        long counter = -1;
        try{
            counter = (long) response;
            log.info("Num de clicks => " + counter);
        } catch (NullPointerException exception) {
            log.info("No se ha podido obtener los clicks: " + exception);
        }
        return counter;
    }

    public GeoLocation getLocationFromIp(String ip) {
        log.info("Pidiendo geolocation al server");

        Object response = template.convertSendAndReceive(exchange.getName(), "rpc", "location:" + ip);

        GeoLocation geoLocation = null;
        try {
            geoLocation = GeoLocation.createGeoLocation((String) response);
        } catch (NullPointerException exception) {
            log.info("No se ha podido obtener los clicks: " + exception);
        }
        return geoLocation;
    }

}
