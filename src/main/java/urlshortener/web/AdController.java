package urlshortener.web;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;

/**
 * Controlador para mostrar la vista HTML de la página publicitaria
 */
@Profile("webapp")
@Controller
public class AdController {

    /**
     * Devuelve la vista de la página intersticial
     * junto con la gestion de la cache en la cabecera de respuesta
     *
     * @param response
     * @return Vista (HTML)
     */
    @Cacheable(cacheNames = "ad")
    @GetMapping("/ad/{.*}")
    public String redirectToAd(HttpServletResponse response) {
        System.out.println("CARGA PAGINA INTERSTICIAL");
        //https://www.baeldung.com/spring-response-header
        //https://developer.mozilla.org/es/docs/Web/HTTP/Headers/Cache-Control
        response.setHeader("Cache-Control", "public, max-age=3600");
        return "ad";
    }

}
