package urlshortener.repository;

import org.springframework.stereotype.Repository;
import urlshortener.domain.GeoLocation;
import urlshortener.domain.SystemInfo;

@Repository
public interface SystemInfoRepository {

    SystemInfo getSystemInfo(GeoLocation location);

}
