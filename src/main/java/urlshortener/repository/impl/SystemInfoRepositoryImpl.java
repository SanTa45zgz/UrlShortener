package urlshortener.repository.impl;

import com.sun.tools.javac.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


import urlshortener.domain.GeoLocation;

import urlshortener.domain.SystemInfo;
import urlshortener.repository.SystemInfoRepository;

import java.util.ArrayList;
import java.util.List;


public class SystemInfoRepositoryImpl implements SystemInfoRepository {


    private static final Logger log = LoggerFactory
            .getLogger(SystemInfoRepositoryImpl.class);

    private final JdbcTemplate jdbc;

    private static final RowMapper<Pair<String, Long>> rowMapper =
            (rs, rowNum) -> new Pair<>(
                    rs.getString("target"), rs.getLong("cuenta")
            );

    public SystemInfoRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public SystemInfo getSystemInfo(GeoLocation location) {
        long numUsers = 0;
        long numClicks = 0;
        long numUris = 0;
        List<Pair<String, Long>> topUris = new ArrayList<>();
        try{
            numClicks = jdbc.queryForObject("select count(*) from click", Long.class);
            numUris = jdbc.queryForObject("select count(*) from shorturl", Long.class);
            numUsers = jdbc.queryForObject("select count(*) from " +
                    "(select ip from click group by ip)", Long.class);
            topUris = jdbc.query("select target, count (*) as cuenta " +
                    "from shorturl right join click on shorturl.hash=click.hash " +
                    "group by target order by count(*) desc limit 10 ", rowMapper );
            
        }catch (Exception e){
            log.debug("When select ", e);
        }
        return new SystemInfo(numUsers, numClicks, numUris,topUris,location);
    }
}
