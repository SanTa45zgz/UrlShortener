package urlshortener.service;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
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

    /**
     * @return Total amount of clicks retrieved from clickRepository
     */
    public long getNumClicks() {
        return clickRepository.count();
    }

    /**
     * @return Total amount of uris that have been shortened retrieved from shortURLRepository
     */
    public long getNumUris() {
        return shortURLRepository.count();
    }

    /**
     * @return Total amount of users connected retrieved from clickRepository
     */
    public long getNumUsers() {
        return clickRepository.countByIp();
    }

    /**
     * @return Top 10 most redirected uris retrieved from systemInfoRepository
     */
    public List<Pair<String, Long>> getHttpRedirects() {
        return systemInfoRepository.getTopUris();
    }


    /**
     * @return Total amount of redirects group by country retrieved from ClickRepository
     */
    public List<Pair<String, Long>> getGeoRedirects() {
        return clickRepository.countByCountry();
    }


    /**
     * @return Total amount of geoLocations retrieved from systemInfoRepository
     */
    public long getTotalGeoLocations() {
        return systemInfoRepository.getCounter("geoLocations");
    }

    /**
     * @return Total amount of urls checked from GoogleSafeBrowsing
     * retrieved from systemInfoRepository
     */
    public long getTotalUrlChecked() {
        return systemInfoRepository.getCounter("urlChecked");
    }

    /**
     * @return Total amount of urls checked from GoogleSafeBrowsing which are safe
     * retrieved from systemInfoRepository
     */
    public long getTotalUrlSafe() {
        return systemInfoRepository.getCounter("urlSafe");
    }


    /* ------------ Increment functions ------------ */

    /**
     * @param geoLocations Amount of geoLocations calculated since last check.
     * @return Result of adding new geoLocations value to the previous amount of geoLocations saved.
     */
    public Long updateTotalGeoLocations(long geoLocations) {
        return systemInfoRepository.updateCounter("geoLocations", geoLocations);
    }

    /**
     * @param urlsChecked Amount of urls that have been checked from BrokerClient since last check
     * @return Result of adding new urlsChecked value to the previous amount of urlsChecked saved.
     */
    public Long updateTotalUrlChecked(long urlsChecked) {
        return systemInfoRepository.updateCounter("urlChecked", urlsChecked);
    }

    /**
     * @param urlsSafe Amount of urls that are safe from BrokerClient since last check.
     * @return Result of adding new urlsSafe value to the previous amount of urlsSafe saved.
     */
    public Long updateTotalUrlSafe(long urlsSafe) {
        return systemInfoRepository.updateCounter("urlSafe", urlsSafe);
    }

}
