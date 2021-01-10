package urlshortener.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import urlshortener.domain.GeoLocation;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

// Service that obtains the geoLocation from the IP address using local database
// GeoIP2 from MaxMind
// https://www.baeldung.com/geolocation-by-ip-with-maxmind
@Profile("worker")
@Service
public class GeoLocationService {

    private static final Logger log = LoggerFactory
            .getLogger(ClickService.class);

    private final DatabaseReader dbReader;

    private long counter = 0;

    public GeoLocationService(ResourceLoader resourceLoader) throws IOException {
        InputStream database = resourceLoader.getResource("classpath:GeoLite2-City.mmdb").getInputStream();
        dbReader = new DatabaseReader.Builder(database).build();
    }

    public GeoLocation getLocation(String ip)
            throws IOException, GeoIp2Exception {
        try{
            CityResponse response = dbReader.city(InetAddress.getByName(ip));
            String cityName = response.getCity().getName();
            String latitude = response.getLocation().getLatitude().toString();
            String longitude = response.getLocation().getLongitude().toString();
            String country = response.getCountry().getName();
            return new GeoLocation(ip, cityName, latitude, longitude, country);
        } catch (AddressNotFoundException e){
            return GeoLocation.geoLocationFixture();
        }
    }

    public long getCounter() {
        long total = counter;
        counter = 0;
        return total;
    }
}
