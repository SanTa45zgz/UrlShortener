package urlshortener.broker;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import urlshortener.service.CacheService;
import urlshortener.service.GeoLocationService;
import urlshortener.service.MetricsService;

import java.io.IOException;

public class BrokerServer {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private final MetricsService metricsService;

    @Autowired
    private final GeoLocationService geoLocationService;

    public BrokerServer(MetricsService metricsService, GeoLocationService geoLocationService) {

        this.metricsService = metricsService;
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
                    return geoLocationService.getLocation(params[1]).toString();
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
                return 1L;
            case "totalSafe":
                return 2L;
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
        // Add to To-Process queue
        cacheService.addUrl(url);
    }

}
