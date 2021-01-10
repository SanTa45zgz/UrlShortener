package urlshortener.service;

import org.springframework.stereotype.Service;
import urlshortener.domain.ShortURL;
import urlshortener.repository.ShortURLRepository;
import urlshortener.web.RedirectController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ShortURLService {

    private final ShortURLRepository shortURLRepository;

    public ShortURLService(ShortURLRepository shortURLRepository) {
        this.shortURLRepository = shortURLRepository;
    }

    public ShortURL findByKey(String id) {
        return shortURLRepository.findByKey(id);
    }

    public ShortURL save(String url, String sponsor, String ip) {
        ShortURL su = ShortURLBuilder.newInstance()
                .target(url)
                .uri((String hash) -> linkTo(methodOn(RedirectController.class).redirectTo(hash, null))
                        .toUri())
                .sponsor(sponsor)
                .createdNow()
                .randomOwner()
                .temporaryRedirect()
                .ip(ip)
                .unknownCountry()
                .build();
        return shortURLRepository.save(su);
    }

    public void updateShortURLSafeness(String url, boolean safe) {
        shortURLRepository.updateSafeness(url, safe);
        System.out.println("Url actualizada ==> " + url + " con seguridad " + safe);
    }
}
