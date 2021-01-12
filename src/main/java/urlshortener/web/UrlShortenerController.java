package urlshortener.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

/**
 * Controlador para gestionar la creación de un nuevo enlace acortado
 */
@Profile("webapp")
@RestController
public class UrlShortenerController {

    private final ShortURLService shortUrlService;

    private final BrokerClient brokerClient;

    public UrlShortenerController(ShortURLService shortUrlService, BrokerClient brokerClient) {
        this.shortUrlService = shortUrlService;
        this.brokerClient = brokerClient;
    }

    /**
     * Método para guardar los datos de un nuevo enlace, así como
     * si un enlace tiene publicidad o no (parámetro sponsor)
     *
     * @param url     URL a acortar
     * @param sponsor <code>true</code> si desea publicidad
     * @param request request para obtener la ip del usuario
     * @return Código HttpStatus (201 si URL válida, 400 si URL no válida)
     */
    @Operation(summary = "Create a shortened link from a valid URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Valid shortened link created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ShortURL.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid shortened link",
                    content = @Content)
    })
    @RequestMapping(value = "/link", method = RequestMethod.POST)
    public ResponseEntity<ShortURL> shortener(@Parameter(description = "URL") @RequestParam("url") String url,
                                              @Parameter(description = "Fill in if you want advertising") @RequestParam(value = "sponsor", required = false)
                                                      String sponsor,
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
        return request.getHeader("x-custom-ip");
    }

}
