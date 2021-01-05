package urlshortener.web;

import javax.servlet.http.HttpServletResponse;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class AdController {

    @Cacheable(cacheNames="ad")
    @GetMapping("/ad/{.*}")
    public String redirectToAd(HttpServletResponse response) {
        System.out.println("CARGA PAGINA INTERSTICIAL");
        //https://www.baeldung.com/spring-response-header
        //https://developer.mozilla.org/es/docs/Web/HTTP/Headers/Cache-Control
        response.setHeader("Cache-Control", "public, max-age=604800, immutable");
        return "ad";
    }

}
