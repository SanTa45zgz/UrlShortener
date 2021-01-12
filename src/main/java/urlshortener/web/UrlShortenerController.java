package urlshortener.web;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import urlshortener.domain.ShortURL;
import urlshortener.service.ShortURLService;

/**
* Controlador para gestionar la creación de un nuevo enlace acortado
* @param shortUrlService
*/
@RestController
public class UrlShortenerController {

  private static final Logger log = LoggerFactory
            .getLogger(UrlShortenerController.class);

  private final ShortURLService shortUrlService;

  public UrlShortenerController(ShortURLService shortUrlService) {
    this.shortUrlService = shortUrlService;
  }

  /**
   * Método para guardar los datos de un nuevo enlace, así como
   * si un enlace tiene publicidad o no (parámetro sponsor)
   * @param url
   * @param sponsor
   * @param request
   * @return Código HttpStatus (201 si URL válida, 400 si URL no válida)
   */
  @Operation(summary = "Create a shortened link from a valid URL")
  @ApiResponses(value = {
    @ApiResponse (responseCode = "201", description = "Valid shortened link created",
    content = {@Content(mediaType = "application/json",
      schema = @Schema(implementation = ShortURL.class)) }),
    @ApiResponse (responseCode = "400", description = "Invalid shortened link",
    content = @Content)
  })
  @RequestMapping(value = "/link", method = RequestMethod.POST)
  public ResponseEntity<ShortURL> shortener(@Parameter(description = "URL") @RequestParam("url") String url,
                                            @Parameter(description = "Fill in if you want advertising") @RequestParam(value = "sponsor", required = false)
                                                String sponsor,
                                            HttpServletRequest request) {
    log.debug(sponsor);
    UrlValidator urlValidator = new UrlValidator(new String[] {"http",
        "https"});
    if (urlValidator.isValid(url)) {
      log.debug("URL VALID");
      ShortURL su = shortUrlService.save(url, sponsor, request.getRemoteAddr());
      HttpHeaders h = new HttpHeaders();
      h.setLocation(su.getUri());
      return new ResponseEntity<>(su, h, HttpStatus.CREATED);
    } else {
      log.debug("URL NOT VALID");
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }
}
