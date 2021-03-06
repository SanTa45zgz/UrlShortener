package urlshortener.repository;

import org.springframework.data.util.Pair;
import urlshortener.domain.Click;

import java.util.List;

public interface ClickRepository {

    List<Click> findByHash(String hash);

    Long clicksByHash(String hash);

    Click save(Click cl);

    void update(Click cl);

    void delete(Long id);

    void deleteAll();

    Long count();

    Long countByIp();

    List<Pair<String, Long>> countByCountry();

    List<Click> list(Long limit, Long offset);

}
