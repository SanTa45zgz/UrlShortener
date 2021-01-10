package urlshortener.web;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener.broker.BrokerClient;
import urlshortener.domain.GeoLocation;
import urlshortener.domain.ShortURL;
import urlshortener.service.ShortURLService;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.concurrent.ExecutionException;

@Profile("webapp")
@RestController
public class UrlShortenerController {
    private final ShortURLService shortUrlService;

    private final ClickService clickService;

    private BrokerClient brokerClient;

    public UrlShortenerController(ShortURLService shortUrlService,
                                  ClickService clickService,
                                  BrokerClient brokerClient) {
        this.shortUrlService = shortUrlService;
        this.clickService = clickService;
        this.brokerClient = brokerClient;
    }

    @RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
    public ResponseEntity<?> redirectTo(@PathVariable String id,
                                        HttpServletRequest request) {
        ShortURL l = shortUrlService.findByKey(id);
        if (l != null) {
            GeoLocation geoLocation = brokerClient.getLocationFromIp(extractIP(request));
            if (geoLocation != null) {
                clickService.saveClick(id, extractIP(request), geoLocation);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
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
            brokerClient.validateUrl(url);
            ShortURL su = shortUrlService.save(url, sponsor, extractIP(request));
            HttpHeaders h = new HttpHeaders();
            h.setLocation(su.getUri());
            return new ResponseEntity<>(su, h, HttpStatus.CREATED);
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
