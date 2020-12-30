package urlshortener.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import urlshortener.domain.GeoLocation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

// Service that obtains the geoLocation from the IP address using local database
// GeoIP2 from MaxMind
// https://www.baeldung.com/geolocation-by-ip-with-maxmind
@Profile("worker")
@Service
public class GeoLocationService {
    private final DatabaseReader dbReader;

    public GeoLocationService(ResourceLoader resourceLoader) throws IOException {
        System.out.println("Creando InputStream de la DB");
        InputStream database = resourceLoader.getResource("classpath:GeoLite2-City.mmdb").getInputStream();
        System.out.println("Creando DatabaseReader");
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
        String country = response.getCountry().getName();
        return new GeoLocation(ip, cityName, latitude, longitude, country);
    }

}
