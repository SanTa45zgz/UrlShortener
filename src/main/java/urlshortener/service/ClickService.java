package urlshortener.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import urlshortener.domain.Click;
import urlshortener.domain.GeoLocation;
import urlshortener.repository.ClickRepository;

@Service
public class ClickService {

    private static final Logger log = LoggerFactory
            .getLogger(ClickService.class);

    private final ClickRepository clickRepository;

    public ClickService(ClickRepository clickRepository) {
        this.clickRepository = clickRepository;
    }

  public void saveClick(String hash, String ip, GeoLocation loc) {
    Click cl = ClickBuilder.newInstance().hash(hash).createdNow().ip(ip).withCountry(loc).build();
    cl = clickRepository.save(cl);
        log.info(cl != null ? "[" + hash + "] saved with id [" + cl.getId() + "] and country ["
            + cl.getCountry() + "]":
        "[" + hash + "] was not saved");
  }

}
