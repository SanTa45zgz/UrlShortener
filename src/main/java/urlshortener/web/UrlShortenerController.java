package urlshortener.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import urlshortener.broker.BrokerClient;
import urlshortener.broker.BrokerConfig;
import urlshortener.domain.GeoLocation;
import org.springframework.web.bind.annotation.*;
import urlshortener.domain.Match;
import urlshortener.domain.ShortURL;
import urlshortener.service.CacheService;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Profile("webapp")
@RestController
public class UrlShortenerController {
    private final ShortURLService shortUrlService;

    private final ClickService clickService;

  private BrokerClient brokerClient;

  public UrlShortenerController(ShortURLService shortUrlService,
                                ClickService clickService,
                                BrokerClient brokerClient)
  {
    this.shortUrlService = shortUrlService;
    this.clickService = clickService;
    this.brokerClient = brokerClient;
  }

  @RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
  public ResponseEntity<?> redirectTo(@PathVariable String id,
                                      HttpServletRequest request) {
    ShortURL l = shortUrlService.findByKey(id);
    if (l != null) {
      try {
        GeoLocation geoLocation = brokerClient.getLocationFromIp(extractIP(request));
        if (geoLocation != null) {
          clickService.saveClick(id, extractIP(request), geoLocation);
        } else {
          return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      return createSuccessfulRedirectToResponse(l);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
    @RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
    public ResponseEntity<?> redirectTo(@PathVariable String id,
                                        HttpServletRequest request) {
        ShortURL l = shortUrlService.findByKey(id);
        if (l != null) {
            clickService.saveClick(id, extractIP(request));
            return createSuccessfulRedirectToResponse(l);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/link", method = RequestMethod.POST)
    public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
                                              @RequestParam(value = "sponsor", required = false)
                                                      String sponsor,
                                              HttpServletRequest request) throws InterruptedException, ExecutionException {
        UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});
        if (urlValidator.isValid(url)) {
            UUID uuid = UUID.randomUUID();
            boolean added = cacheService.addUrl(uuid, url);
            if (added) {
                Future<Optional<Match>> matchFuture = cacheService.waitResponse(uuid);
                if (!matchFuture.get().isPresent()) {
                    ShortURL su = shortUrlService.save(url, sponsor, extractIP(request));
                    HttpHeaders h = new HttpHeaders();
                    h.setLocation(su.getUri());
                    return new ResponseEntity<>(su, h, HttpStatus.CREATED);
                } else {
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private String extractIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
        HttpHeaders h = new HttpHeaders();
        h.setLocation(URI.create(l.getTarget()));
        return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
    }
}
