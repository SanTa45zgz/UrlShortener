package urlshortener.config;

import com.sun.tools.javac.util.Pair;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;
import urlshortener.repository.ClickRepository;
import urlshortener.repository.ShortURLRepository;
import urlshortener.repository.SystemInfoRepository;
import urlshortener.service.ClickService;

import java.util.List;

@Component
public class PrometheusCounters {

    private final MeterRegistry registry;
    private final ShortURLRepository shortURLRepository;
    private final ClickRepository clickRepository;
    private final SystemInfoRepository systemInfoRepository;

    private static final Logger log = LoggerFactory
            .getLogger(ClickService.class);


    public PrometheusCounters(MeterRegistry meterRegistry, SystemInfoRepository systemInfoRepository,
                              ClickRepository clickRepository, ShortURLRepository shortURLRepository){
        this.registry = meterRegistry;
        this.clickRepository = clickRepository;
        this.shortURLRepository = shortURLRepository;
        this.systemInfoRepository = systemInfoRepository;
    }
    @Scheduled(fixedRate = 5000)
    public void countNumClick(){
        Gauge.builder("sysinfo_clicks_count", clickRepository::count)
                .description("Total amount of clicks")
                .register(registry);
        log.info("NumClicks calculados");
    }

    @Scheduled(fixedRate = 5000)
    public void countNumUris(){
        Gauge.builder("sysinfo_uris_count", shortURLRepository::count)
                .description("Total amount of uris shortened")
                .register(registry);
    }

    @Scheduled(fixedRate = 5000)
    public void countNumUsers(){
        Gauge.builder("sysinfo_users_count", clickRepository::countByIp)
                .description("Total amount of users connected")
                .register(registry);
    }

    @Scheduled(fixedDelay = 5000)
    public void http_redirects(){
        List<Pair<String, Long>> topUris = getRankingUris();
        for (Pair<String, Long> item : topUris) {
            registry.counter("http_redirects", "hash", item.fst, "value", item.snd.toString()).increment();
        }
    }

    private List<Pair<String, Long>> getRankingUris(){
        return systemInfoRepository.getTopUris();
    }

}
