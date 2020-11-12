package urlshortener.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import org.springframework.stereotype.Service;
import urlshortener.domain.GeoLocation;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

// Service that obtains the geoLocation from the IP address using local database
// GeoIP2 from MaxMind
// https://www.baeldung.com/geolocation-by-ip-with-maxmind
@Service
public class GeoLocationService {
    private DatabaseReader dbReader;

    public GeoLocationService() throws IOException {
        File database = new File("src/main/resources/GeoLite2-City.mmdb");
        dbReader = new DatabaseReader.Builder(database).build();
    }

    public GeoLocation getLocation(String ip)
            throws IOException, GeoIp2Exception {
        CityResponse response = dbReader.city(InetAddress.getByName(ip));

        String cityName = response.getCity().getName();
        String latitude =
                response.getLocation().getLatitude().toString();
        String longitude =
                response.getLocation().getLongitude().toString();
        return new GeoLocation(ip, cityName, latitude, longitude);
    }

}
