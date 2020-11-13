package urlshortener.web;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
public class UrlShortenerController {
    private final ShortURLService shortUrlService;

    private final ClickService clickService;

    private final CacheService cacheService;

    public UrlShortenerController(ShortURLService shortUrlService, ClickService clickService, CacheService cacheService) {
        this.shortUrlService = shortUrlService;
        this.clickService = clickService;
        this.cacheService = cacheService;
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
        UrlValidator urlValidator = new UrlValidator(new String[]{"http",
                "https"});
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
                return new ResponseEntity<>(HttpStatus.INSUFFICIENT_STORAGE);
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
