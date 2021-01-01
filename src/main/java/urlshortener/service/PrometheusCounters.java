package urlshortener.service;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import urlshortener.broker.BrokerClient;

import java.util.List;

@Profile("webapp")
@Component
public class PrometheusCounters {

    private final MeterRegistry registry;

    private final BrokerClient brokerClient;

    private final MetricsService metricsService;

    private static final Logger log = LoggerFactory
            .getLogger(ClickService.class);

    public PrometheusCounters(MeterRegistry meterRegistry, BrokerClient brokerClient, MetricsService metricsService){
        this.registry = meterRegistry;

        this.brokerClient = brokerClient;
        this.metricsService = metricsService;
    }
    @Scheduled(fixedRate = 1000)
    public void countNumClick(){
        long clicks = brokerClient.getNumClicksFromServers();
        // TODO: Igual hay que cambiarlos
        metricsService.saveNewClicks(clicks);
        Gauge.builder("sysinfo.clicks", metricsService.getNewClicks(), value -> value)
                .description("Total amount of clicks")
                .register(registry);
//        log.info("NumClicks calculated");
    }

    @Scheduled(fixedRate = 1000)
    public void countNumUris(){
        long uris = brokerClient.countNumUris();
        Gauge.builder("sysinfo.uris", uris, value -> value)
                .description("Total amount of uris shortened")
                .register(registry);
//        log.info("NumUris shortened calculated");
    }

    @Scheduled(fixedRate = 1000)
    public void countNumUsers(){
        long users = brokerClient.countUsers();
        Gauge.builder("sysinfo.users", users, value -> value)
                .description("Total amount of users connected")
                .register(registry);
//        log.info("NumUsers calculated");
    }

    @Scheduled(fixedDelay = 1000)
    public void http_redirects(){
        List<Pair<String, Long>> topUris = brokerClient.getRankingUris();
        for (Pair<String, Long> item : topUris) {
            registry.counter("http.redirects",
                    "hash", item.getFirst(),
                    "value", item.getSecond().toString())
                    .increment();
        }
//        log.info("TopUris calculated");
    }

    @Scheduled(fixedDelay = 1000)
    public void geo_redirects(){
        List<Pair<String, Long>> country_redirects = brokerClient.getCountryCount();
        for (Pair<String, Long> item : country_redirects) {
            registry.counter("geo.redirects",
                    "country", item.getFirst(),
                    "value", item.getSecond().toString())
                    .increment();
        }
//        log.info("GeoRedirects calculated");
    }

}
