package urlshortener.repository;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemInfoRepository {

    List<Pair<String, Long>> getTopUris();

    Long updateCounter(String key, long value);

    Long getCounter(String key);
}
