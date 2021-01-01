package urlshortener.broker;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import urlshortener.service.GeoLocationService;
import urlshortener.service.MetricsService;

import java.io.IOException;

public class BrokerServer  {

    @Autowired
    private final MetricsService metricsService;

    @Autowired
    private final GeoLocationService geoLocationService;

    public BrokerServer(MetricsService metricsService, GeoLocationService geoLocationService) {

        this.metricsService = metricsService;
        this.geoLocationService = geoLocationService;
    }

    @RabbitListener(queues = "url.shortener.rpc.requests")
    // @SendTo("url.shortener.rpc.replies") used when the
    // client doesn't set replyTo.
    public Object receiver(String message) {
        String[] params = message.split(":");
        String option = params[0];

        System.out.println("[*] Instrucción recibida: " + option);
        switch(option) {
            case "location":
                try {
                    return geoLocationService.getLocation(params[1]);
                } catch (GeoIp2Exception | IOException e) {
                    e.printStackTrace();
                }
                return null;
            case "clicks":
                return metricsService.getNumClicks();
            case "uris":
                return metricsService.getNumUris();
            case "users":
                return metricsService.getNumUsers();
            case "httpRedirects":
                return metricsService.getHttpRedirects();
            case "geoRedirects":
                return metricsService.getGeoRedirects();
            default:
                return null;
        }
    }

}
