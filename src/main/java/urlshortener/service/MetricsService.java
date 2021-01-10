package urlshortener.service;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import urlshortener.domain.RedirectList;
import urlshortener.repository.ClickRepository;
import urlshortener.repository.ShortURLRepository;
import urlshortener.repository.SystemInfoRepository;

import java.util.List;

@Service
public class MetricsService {

    private final ShortURLRepository shortURLRepository;
    private final ClickRepository clickRepository;
    private final SystemInfoRepository systemInfoRepository;


    public MetricsService(ShortURLRepository shortURLRepository, ClickRepository clickRepository, SystemInfoRepository systemInfoRepository) {
        this.shortURLRepository = shortURLRepository;
        this.clickRepository = clickRepository;
        this.systemInfoRepository = systemInfoRepository;
    }

    /* ------------ Fetch functions ------------- */

    public long getNumClicks() {
        return clickRepository.count();
    }

    public long getNumUris() {
        return shortURLRepository.count();
    }

    public long getNumUsers() {
        return clickRepository.countByIp();
    }

    public List<Pair<String, Long>> getHttpRedirects() {
        return systemInfoRepository.getTopUris();
    }

    public List<Pair<String, Long>> getGeoRedirects() {
        return clickRepository.countByCountry();
    }

    public long getTotalGeoLocations() {
        return systemInfoRepository.getCounter("geoLocations");
    }

    public long getTotalUrlChecked() {
        return systemInfoRepository.getCounter("urlChecked");
    }

    public long getTotalUrlSafe() {
        return systemInfoRepository.getCounter("urlSafe");
    }

    /* ------------ Increment functions ------------ */

    public Long updateTotalGeoLocations(long geoLocations) {
        return systemInfoRepository.updateCounter("geoLocations", geoLocations);
    }

    public Long updateTotalUrlChecked(long urlsChecked) {
        return systemInfoRepository.updateCounter("urlChecked", urlsChecked);
    }

    public Long updateTotalUrlSafe(long urlsSafe) {
        return systemInfoRepository.updateCounter("urlSafe", urlsSafe);
    }

}
