package urlshortener.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import urlshortener.broker.BrokerClient;
import urlshortener.domain.GeoLocation;
import urlshortener.domain.ShortURL;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Controlador para gestionar la redirección de las URL
 */
@Profile("webapp")
@RestController
public class RedirectController {

    private static final Logger log = LoggerFactory
            .getLogger(RedirectController.class);

    private final ShortURLService shortUrlService;
    private final ClickService clickService;
    private final BrokerClient brokerClient;

    public RedirectController(ShortURLService shortUrlService, ClickService clickService, BrokerClient brokerClient) {
        this.shortUrlService = shortUrlService;
        this.clickService = clickService;
        this.brokerClient = brokerClient;
    }

    /**
     * Realiza la redirección en función de si se trata de un enlace
     * acortado con o sin publicidad y dependiendo de si el enlace existe
     * y la dirección de destino es segura
     *
     * @param id Shortened url id/hash
     * @return Redirección a página web destino (307),
     * Redirección a página publicitaria (302),
     * Error 404 si no existe y/o no es segura
     */
    @Operation(summary = "Redirect to ad page or destination URL depending on whether it is an ad shortened URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirection to interstitial page",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "307", description = "Redirection to destination page",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "URL not found",
                    content = {@Content(mediaType = "application/json")}),
    })
    @GetMapping("/{id:(?!link|index).*}")
    public ResponseEntity<?> redirectTo(@Parameter(description = "Shortened url id/hash") @PathVariable String id,
                                        HttpServletRequest request) {
        ShortURL l = shortUrlService.findByKey(id);
        if (l != null && l.getSafe() != null && l.getSafe()) {
            GeoLocation geoLocation = brokerClient.getLocationFromIp(extractIP(request));
            if (geoLocation != null) {
                clickService.saveClick(id, extractIP(request), geoLocation);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            if (l.getSponsor() != null) {
                return createSuccessfulRedirectToResponse(id);
            } else {
                return createSuccessfulRedirectToResponse(l);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Método para obtener la URL destino a través del hash de un enlace acortado
     *
     * @param id Shortened url id/hash
     * @return URL destino
     */
    @Operation(summary = "Get the destination URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Destination URL",
                    content = {@Content(mediaType = "string",
                            schema = @Schema(implementation = String.class))})
    })
    @GetMapping(value = "/redirect")
    public ResponseEntity<String> toLink(@Parameter(description = "Shortened url id/hash") @RequestParam("hash") String id,
                                         HttpServletRequest request) {
        log.debug("Redireccionamos tras publi");
        ShortURL l = shortUrlService.findByKey(id);
        return new ResponseEntity<>(l.getTarget(), HttpStatus.OK);
    }

    private String extractIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    /**
     * Realiza la redirección a la página publicitariaa del enlace acortado
     * a través de su id. Además, devuelve una cabecera para que el navegador
     * realice la gestión de cache pertinente
     *
     * @param id
     * @return redirección a URL de página publicitaria
     */
    @Cacheable(cacheNames = "toAd")
    public ResponseEntity<?> createSuccessfulRedirectToResponse(String id) {
        log.debug("REDIRECCION A PAGINA INTERSTICIAL");
        HttpHeaders h = new HttpHeaders();
        h.add("Location", "ad/" + id);
        //https://www.baeldung.com/spring-response-header
        //https://developer.mozilla.org/es/docs/Web/HTTP/Headers/Cache-Control
        CacheControl cacheControl = CacheControl.maxAge(60, TimeUnit.MINUTES)
                .cachePublic();
        h.setCacheControl(cacheControl);
        return new ResponseEntity<>(h, HttpStatus.FOUND);
    }

    /**
     * Realiza la redirección a la página web destino
     *
     * @param shortURL url acortada
     * @return redirección a URL de página destino
     */
    private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL shortURL) {
        HttpHeaders h = new HttpHeaders();
        h.setLocation(URI.create(shortURL.getTarget()));
        return new ResponseEntity<>(h, HttpStatus.valueOf(shortURL.getMode()));
    }

}
