package urlshortener.broker;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import urlshortener.service.CacheService;
import urlshortener.service.GeoLocationService;

import java.io.IOException;

public class BrokerServer {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private final GeoLocationService geoLocationService;

    public BrokerServer(GeoLocationService geoLocationService) {
        this.geoLocationService = geoLocationService;
    }

    @RabbitListener(queues = "url.shortener.rpc.requests")
    public Object receiver(String message) {
        String[] params = message.split(":");
        String option = params[0];

        System.out.println("[*] Instrucción recibida: " + option);
        switch (option) {
            case "location":
                try {
                    return geoLocationService.getLocation(message.substring(9)).toString();
                } catch (GeoIp2Exception | IOException e) {
                    e.printStackTrace();
                }
                return null;
            case "validate":
                validateUrl(message);
                return null;
            case "totalGeoLocate":
                return geoLocationService.getCounter();
            case "totalChecked":
                return cacheService.getUrlsChecked();
            case "totalSafe":
                return cacheService.getUrlsSafe();
            default:
                System.out.println("No se reconoce este comando ==> " + option);
                return null;
        }
    }

    /**
     * Extracts the url form the message received and adds the url received to the cache queue to be processed
     * @param message Message received to extract the url
     */
    private void validateUrl(String message) {
        String url = message.substring(9);
        System.out.println("Se añade la siguiente url para validar => " + url);
        // Add to To-Process queue
        cacheService.addUrl(url);
    }

}
