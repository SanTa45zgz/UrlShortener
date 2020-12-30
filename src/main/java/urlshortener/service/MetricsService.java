package urlshortener.service;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import urlshortener.domain.Click;
import urlshortener.domain.GeoLocation;
import urlshortener.repository.ClickRepository;
import urlshortener.repository.ShortURLRepository;
import urlshortener.repository.SystemInfoRepository;

import java.io.IOException;
import java.util.List;

@Profile("worker")
@Service
public class MetricsService {

    private final MeterRegistry registry;
    private final ShortURLRepository shortURLRepository;
    private final ClickRepository clickRepository;
    private final SystemInfoRepository systemInfoRepository;
    private final GeoLocationService geoLocationService;


    public MetricsService(MeterRegistry registry, ShortURLRepository shortURLRepository, ClickRepository clickRepository, SystemInfoRepository systemInfoRepository, GeoLocationService geoLocationService) {
        this.registry = registry;
        this.shortURLRepository = shortURLRepository;
        this.clickRepository = clickRepository;
        this.systemInfoRepository = systemInfoRepository;
        this.geoLocationService = geoLocationService;
    }

    public long getNumClicks() {
        List<Click> clicks = clickRepository.list(100L, 0L);
        return clickRepository.count();
    }

    public String getLocation(String ip){
        String result = "";
        try {
            GeoLocation geoLocation = geoLocationService.getLocation(ip);
            result = geoLocation.toString();
        } catch (IOException | GeoIp2Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
