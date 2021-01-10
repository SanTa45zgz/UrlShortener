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

    public PrometheusCounters(MeterRegistry meterRegistry, BrokerClient brokerClient, MetricsService metricsService) {
        this.registry = meterRegistry;

        this.brokerClient = brokerClient;
        this.metricsService = metricsService;
    }

    @Scheduled(fixedRate = 1000)
    public void countNumClick(){
        Gauge.builder("sysinfo.clicks", metricsService::getNumClicks)
                .description("Total amount of clicks")
                .register(registry);
        log.info("NumClicks calculated");
    }

    @Scheduled(fixedRate = 1000)
    public void countNumUris(){
        Gauge.builder("sysinfo.uris", metricsService::getNumUris)
                .description("Total amount of uris shortened")
                .register(registry);
        log.info("NumUris shortened calculated");
    }

    @Scheduled(fixedRate = 1000)
    public void countNumUsers(){
        Gauge.builder("sysinfo.users", metricsService::getNumUsers)
                .description("Total amount of users connected")
                .register(registry);
        log.info("NumUsers calculated");
    }

    @Scheduled(fixedDelay = 1000)
    public void http_redirects(){
        List<Pair<String, Long>> topUris = getRankingUris();
        for (Pair<String, Long> item : topUris) {
            registry.counter("http.redirects",
                    "hash", item.getFirst(),
                    "value", item.getSecond().toString())
                    .increment();
        }
        log.info("TopUris calculated");
    }

    @Scheduled(fixedDelay = 1000)
    public void geo_redirects(){
        List<Pair<String, Long>> country_redirects = getCountryCount();
        for (Pair<String, Long> item : country_redirects) {
            registry.counter("geo.redirects",
                    "country", item.getFirst(),
                    "value", item.getSecond().toString())
                    .increment();
        }
        log.info("GeoRedirects calculated");
    }

    private List<Pair<String, Long>> getRankingUris(){
        return metricsService.getHttpRedirects();
    }
    private List<Pair<String, Long>> getCountryCount(){
        return metricsService.getGeoRedirects();
    }

    @Scheduled(fixedRate = 1000)
    public void countTotalGeoLocations() {
        long geoLocations = brokerClient.getTotalGeoLocations();
        long newGeoLocations = metricsService.updateTotalGeoLocations(geoLocations);
        Gauge.builder("sysinfo.totalGeoLocations", newGeoLocations, value -> value)
                .description("Total amount of geoLocations calculated")
                .register(registry);
//        log.info("TotalGeoLocations calculated");
    }

    @Scheduled(fixedRate = 1000)
    public void countTotalUrlsChecked() {
        long urlsChecked = brokerClient.getTotalUrlsChecked();
        long newUrlsChecked = metricsService.updateTotalGeoLocations(urlsChecked);
        Gauge.builder("sysinfo.totalUrlsChecked", newUrlsChecked, value -> value)
                .description("Total amount of urls checked")
                .register(registry);
//        log.info("TotalGeoLocations calculated");
    }

    @Scheduled(fixedRate = 1000)
    public void countTotalUrlsSafe() {
        long urlsSafe = brokerClient.getTotalUrlsSafe();
        long newUrlsSafe = metricsService.updateTotalGeoLocations(urlsSafe);
        Gauge.builder("sysinfo.totalUrlsSafe", newUrlsSafe, value -> value)
                .description("Total amount of urls safe")
                .register(registry);
//        log.info("TotalGeoLocations calculated");
    }

}
