package urlshortener.repository;

import com.sun.tools.javac.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemInfoRepository {

    List<Pair<String, Long>> getTopUris();
}
