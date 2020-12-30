package urlshortener.broker;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import urlshortener.service.MetricsService;

public class BrokerServer  {

    @Autowired
    private final MetricsService metricsService;

    public BrokerServer(MetricsService metricsService) {

        this.metricsService = metricsService;
    }

    @RabbitListener(queues = "url.shortener.rpc.requests")
    // @SendTo("url.shortener.rpc.replies") used when the
    // client doesn't set replyTo.
    public Object receiver(String message) {
        String[] params = message.split(":");
        String option = params[0];

        System.out.println("[*] Instrucción recibida: " + option);
        switch(option) {
            case "clicks":
                return metricsService.getNumClicks();
            case "location":
                return metricsService.getLocation(params[1]);

            default:
                return null;
        }
    }

}
