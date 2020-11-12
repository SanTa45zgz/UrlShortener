package urlshortener.web;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import urlshortener.domain.GeoLocation;
import urlshortener.domain.SystemInfo;
import urlshortener.repository.SystemInfoRepository;
import urlshortener.service.GeoLocationService;

import java.net.*;
import java.io.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class SystemInfoController {


    private final SystemInfoRepository systemInfoRepository;

    private final GeoLocationService geoLocationService;

    public SystemInfoController(SystemInfoRepository systemInfoRepository,
                                GeoLocationService geoLocationService) throws IOException {
        this.systemInfoRepository = systemInfoRepository;
        this.geoLocationService = geoLocationService;

    }

    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    public ResponseEntity<SystemInfo> getSystemInfo() throws IOException, GeoIp2Exception {
        GeoLocation loc;
        if (geoLocationService != null)
            loc = geoLocationService.getLocation(extractIP());
        else
            loc = null;
        SystemInfo serverInfo = systemInfoRepository.getSystemInfo(loc);
        return new ResponseEntity<>(serverInfo, HttpStatus.OK);
    }

    private String extractIP() throws IOException {


        URL whatIsMyIp = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                whatIsMyIp.openStream()));

        String ip = in.readLine(); //you get the IP as a String
        System.out.println(ip);
        return ip;
    }

}
