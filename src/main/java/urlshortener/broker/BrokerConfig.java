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
import urlshortener.service.ShortURLService;

@Configuration
public class BrokerConfig {

    @Bean
    public Queue queue() {
        return new Queue("url.shortener.rpc.requests");
    }

    @Bean
    public Queue responseQueue() {
        return new Queue("url.shortener.rpc.responses.responses");
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("url.shortener.rpc");
    }

    @Bean
    public DirectExchange exchangeResponses() {
        return new DirectExchange("url.shortener.rpc.responses");
    }

    @Bean
    public Binding binding(DirectExchange exchange,
                           Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("rpc");
    }

    @Bean
    public Binding responseBinding(DirectExchange exchangeResponses,
                           Queue responseQueue) {
        return BindingBuilder.bind(responseQueue)
                .to(exchangeResponses)
                .with("rpc-responses");
    }

    @Profile("webapp")
    @Bean
    public BrokerClient brokerClient() {
        return new BrokerClient();
    }

    @Profile("webapp")
    @Bean
    public BrokerSafeness brokerSafeness(ShortURLService shortURLService) {
        return new BrokerSafeness(shortURLService);
    }

    @Profile("worker")
    @Bean
    public BrokerServer brokerServer(ShortURLRepository shortURLRepository, ClickRepository clickRepository,
                                     SystemInfoRepository systemInfoRepository, GeoLocationService geoLocationService) {
        return new BrokerServer(new MetricsService(shortURLRepository, clickRepository, systemInfoRepository), geoLocationService);
    }

}
