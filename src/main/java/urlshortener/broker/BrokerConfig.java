package urlshortener.broker;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import urlshortener.repository.ClickRepository;
import urlshortener.repository.ShortURLRepository;
import urlshortener.repository.SystemInfoRepository;
import urlshortener.service.GeoLocationService;
import urlshortener.service.MetricsService;

@Configuration
public class BrokerConfig {

    @Bean
    public Queue queue() {
        return new Queue("url.shortener.rpc.requests");
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("url.shortener.rpc");
    }

    @Bean
    public Binding binding(DirectExchange exchange,
                           Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("rpc");
    }

    @Profile("webapp")
    @Bean
    BrokerClient brokerClient() {
        System.out.println("Creando BrokerClient");
        return new BrokerClient();
    }

    @Profile("worker")
    @Bean
    public BrokerServer brokerServer(ShortURLRepository shortURLRepository, ClickRepository clickRepository,
                                     SystemInfoRepository systemInfoRepository, GeoLocationService geoLocationService) {
        return new BrokerServer(new MetricsService(shortURLRepository, clickRepository, systemInfoRepository), geoLocationService);
    }

}
