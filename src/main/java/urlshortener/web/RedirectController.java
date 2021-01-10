package urlshortener.web;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import urlshortener.domain.ShortURL;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;


@Controller
public class RedirectController {
    private final ShortURLService shortUrlService;
    private final ClickService clickService;

    public RedirectController(ShortURLService shortUrlService, ClickService clickService) {
        this.shortUrlService = shortUrlService;
        this.clickService = clickService;
    }
    
    @GetMapping("/{id:(?!link|index).*}")
    public ResponseEntity<?> redirectTo(@PathVariable String id,
                                        HttpServletRequest request) {
        ShortURL l = shortUrlService.findByKey(id);
        if (l != null && l.getSponsor() != null){
            return createSuccessfulRedirectToResponse(id);
        }
        else{
            if (l != null) {
                clickService.saveClick(id, extractIP(request));
                return createSuccessfulRedirectToResponse(l);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
    }

    @GetMapping(value = "/redirect/{hash}")
    public ResponseEntity<String> toLink(@RequestParam("hash") String id,
                                            HttpServletRequest request) {
        System.out.println("Redireccionamos tras publi");
        ShortURL l = shortUrlService.findByKey(id);
        clickService.saveClick(id, extractIP(request));
        return new ResponseEntity<String>(l.getTarget(),HttpStatus.OK);
    }

    private String extractIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    @Cacheable(cacheNames="toAd")
    private ResponseEntity<?> createSuccessfulRedirectToResponse(String id) {
        System.out.println("REDIRECCION A PAGINA INTERSTICIAL");
        HttpHeaders h = new HttpHeaders();
        h.add("Location", "ad/"+id);
        //https://www.baeldung.com/spring-response-header
        //https://developer.mozilla.org/es/docs/Web/HTTP/Headers/Cache-Control
        CacheControl cacheControl = CacheControl.maxAge(60, TimeUnit.MINUTES)
            .cachePublic();
        h.setCacheControl(cacheControl);
        return new ResponseEntity<>(h, HttpStatus.FOUND);
    }

    private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
        HttpHeaders h = new HttpHeaders();
        h.setLocation(URI.create(l.getTarget()));
        return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
    }

}
