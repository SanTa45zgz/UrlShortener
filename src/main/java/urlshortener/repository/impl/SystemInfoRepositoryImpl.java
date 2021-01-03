package urlshortener.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import urlshortener.repository.SystemInfoRepository;

import java.util.ArrayList;
import java.util.List;


public class SystemInfoRepositoryImpl implements SystemInfoRepository {


    private static final Logger log = LoggerFactory
            .getLogger(SystemInfoRepositoryImpl.class);

    private final JdbcTemplate jdbc;

    private static final RowMapper<Pair<String, Long>> rowMapper =
            (rs, rowNum) -> Pair.of(
                    rs.getString("hash"), rs.getLong("cuenta")
            );

    public SystemInfoRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Pair<String, Long>> getTopUris() {
        List<Pair<String, Long>> topUris = new ArrayList<>();
        try {
            topUris = jdbc.query("select hash, count (*) as cuenta " +
                    "from shorturl right join click on shorturl.hash=click.hash " +
                    "group by hash order by count(*) desc limit 10 ", rowMapper);
        } catch (Exception e) {
            log.debug("When select ", e);
        }
        return topUris;
    }

    @Override
    public Long updateCounter(String key, long value) {
        try {
            jdbc.update("update counters set value=value+? where key=?", value, key);
            return getCounter(key);
        } catch (Exception e) {
            log.debug("Fallo al actualizar contador de " + key);
        }
        return -1L;
    }

    @Override
    public Long getCounter(String key) {
        try {
            return jdbc
                    .queryForObject("select value from counters where key = ?", new Object[]{key},
                            Long.class);
        } catch (Exception e) {
            log.debug("When counting " + key, e);
        }
        return -1L;
    }

}
