package urlshortener.service;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import urlshortener.broker.BrokerClient;

@Profile("webapp")
@Component
public class PrometheusCounters {

    private final MeterRegistry registry;

    private BrokerClient brokerClient;

    private static final Logger log = LoggerFactory
            .getLogger(ClickService.class);

    public PrometheusCounters(MeterRegistry meterRegistry, BrokerClient brokerClient){
        this.registry = meterRegistry;

        this.brokerClient = brokerClient;
    }
    @Scheduled(fixedRate = 1000)
    public void countNumClick(){
        long clicks = brokerClient.getNumClicksFromServers();
//        Gauge.builder("sysinfo.clicks", brokerClient::getNumClicksFromServers)
//                .description("Total amount of clicks")
//                .register(registry);
        log.info("NumClicks calculated: " + clicks);
    }

//    @Scheduled(fixedRate = 1000)
//    public void countNumUris(){
//        Gauge.builder("sysinfo.uris", shortURLRepository::count)
//                .description("Total amount of uris shortened")
//                .register(registry);
//        log.info("NumUris shortened calculated");
//    }
//
//    @Scheduled(fixedRate = 1000)
//    public void countNumUsers(){
//        Gauge.builder("sysinfo.users", clickRepository::countByIp)
//                .description("Total amount of users connected")
//                .register(registry);
//        log.info("NumUsers calculated");
//    }
//
//    @Scheduled(fixedDelay = 1000)
//    public void http_redirects(){
//        List<Pair<String, Long>> topUris = getRankingUris();
//        for (Pair<String, Long> item : topUris) {
//            registry.counter("http.redirects",
//                    "hash", item.fst,
//                    "value", item.snd.toString())
//                    .increment();
//        }
//        log.info("TopUris calculated");
//    }
//
//    @Scheduled(fixedDelay = 1000)
//    public void geo_redirects(){
//        List<Pair<String, Long>> country_redirects = getCountryCount();
//        for (Pair<String, Long> item : country_redirects) {
//            registry.counter("geo.redirects",
//                    "country", item.fst,
//                    "value", item.snd.toString())
//                    .increment();
//        }
//        log.info("GeoRedirects calculated");
//    }
//
//    private List<Pair<String, Long>> getRankingUris(){
//        return systemInfoRepository.getTopUris();
//    }
//    private List<Pair<String, Long>> getCountryCount(){
//        return clickRepository.countByCountry();
//    }

}
