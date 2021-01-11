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

    public void validateUrl(String url) {
        template.convertAndSend(exchange.getName(),"rpc", "validate:" + url);
    }

    /* -------------------------------- */
    /* ---------- Statistics ---------- */
    /* -------------------------------- */

    public long getTotalGeoLocations() {

        Object response = template.convertSendAndReceive(exchange.getName(), "rpc", "totalGeoLocate:dummy");

        long counter = 0;
        try {
            counter = (long) response;
//            log.info("Num de geoLocations => " + counter);
        } catch (NullPointerException exception) {
            log.info("No se ha podido obtener el contador de geo localizaciones realizadas: " + exception);
        }
        return counter;
    }

    public long getTotalUrlsChecked() {

        Object response = template.convertSendAndReceive(exchange.getName(), "rpc", "totalChecked:dummy");

        long counter = 0;
        try {
            counter = (long) response;
//            log.info("Num de urls comprobadas => " + counter);
        } catch (NullPointerException exception) {
            log.info("No se ha podido obtener el contador de urls comprobadas: " + exception);
        }
        return counter;
    }

    public long getTotalUrlsSafe() {

        Object response = template.convertSendAndReceive(exchange.getName(), "rpc", "totalSafe:dummy");

        long counter = 0;
        try {
            counter = (long) response;
//            log.info("Num de urls seguras => " + counter);
        } catch (NullPointerException exception) {
            log.info("No se ha podido obtener el contador de urls seguras: " + exception);
        }
        return counter;
    }

}
