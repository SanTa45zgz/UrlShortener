package urlshortener.web;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.cache.annotation.Cacheable;
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
            //return "ad"; // Busca la vista "a-view" (normalmente en resources/templates), la genera inyectando el modelo y la devuelve una representación con un 200
        }
        else{
            if (l != null) {
                System.out.println("ENTRAMOS");
                clickService.saveClick(id, extractIP(request));
                return createSuccessfulRedirectToResponse(l);
                //return "redirect:"+l.getTarget(); // devuelve un 302 con un Location: http://some.page.web/
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
    }

    @Cacheable(cacheNames="ad")
    @GetMapping("/ad/{.*}")
    public String redirectToAd(HttpServletResponse response) {
        System.out.println("CARGA PAGINA INTERSTICIAL");
        //https://www.baeldung.com/spring-response-header
        //https://developer.mozilla.org/es/docs/Web/HTTP/Headers/Cache-Control
        response.setHeader("Cache-Control", "public, max-age=604800, immutable");
        return "ad";
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
        h.set("Cache-Control", "public, max-age=604800, immutable");
        return new ResponseEntity<>(h, HttpStatus.FOUND);
    }

    private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
        HttpHeaders h = new HttpHeaders();
        h.setLocation(URI.create(l.getTarget()));
        return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
    }

}
