package urlshortener.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import urlshortener.repository.ClickRepository;
import urlshortener.repository.ShortURLRepository;
import urlshortener.repository.SystemInfoRepository;

@Configuration
public class BasicCounters {
    @Bean
    public Gauge countNumClick(MeterRegistry registry, ClickRepository clickRepository){
        return Gauge
                .builder("sysinfo_clicks_count", clickRepository::count )
                .description("Total amount of clicks")
                .register(registry);
    }

    @Bean
    public Gauge countNumUris(MeterRegistry registry, ShortURLRepository shortURLRepository){
        return Gauge
                .builder("sysinfo_uris_count", shortURLRepository::count )
                .description("Total amount of uris shortened")
                .register(registry);
    }
    @Bean
    public Gauge countNumUsers(MeterRegistry registry, ClickRepository clickRepository){
       return Gauge
               .builder("sysinfo_users_count", clickRepository::countByIp)
               .description("Total amount of users connected")
               .register(registry);
    }

}
