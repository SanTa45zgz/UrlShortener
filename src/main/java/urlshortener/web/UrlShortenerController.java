package urlshortener.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import urlshortener.broker.BrokerClient;
import urlshortener.domain.ShortURL;
import urlshortener.service.ShortURLService;

import javax.servlet.http.HttpServletRequest;

@Profile("webapp")
@RestController
public class UrlShortenerController {
    private final ShortURLService shortUrlService;

    private final BrokerClient brokerClient;

    public UrlShortenerController(ShortURLService shortUrlService, BrokerClient brokerClient) {
        this.shortUrlService = shortUrlService;
        this.brokerClient = brokerClient;
    }

    @Operation()
    @RequestMapping(value = "/link", method = RequestMethod.POST)
    public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
                                              @RequestParam(value = "sponsor", required = false) String sponsor,
                                              HttpServletRequest request) {
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

}
