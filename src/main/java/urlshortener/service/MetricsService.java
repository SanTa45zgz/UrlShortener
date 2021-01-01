package urlshortener.service;

import org.springframework.stereotype.Service;
import urlshortener.domain.RedirectList;
import urlshortener.repository.ClickRepository;
import urlshortener.repository.ShortURLRepository;
import urlshortener.repository.SystemInfoRepository;

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
        return  clickRepository.countByIp();
    }

    public String getHttpRedirects() {
        RedirectList redirectList = new RedirectList(systemInfoRepository.getTopUris());
        return redirectList.toString();
    }

    public String getGeoRedirects() {
        RedirectList redirectList = new RedirectList(clickRepository.countByCountry());
        return redirectList.toString();
    }

    /* ------------ Increment functions ------------ */

    public void saveNewClicks(long clicks) {
        clickRepository.updateCounter("clicks", clicks);
    }

    public Long getNewClicks() {
        return  clickRepository.getCounter("clicks");
    }

}
