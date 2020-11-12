package urlshortener.repository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import urlshortener.domain.SystemInfo;
import urlshortener.fixtures.ShortURLFixture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import urlshortener.repository.impl.ShortURLRepositoryImpl;
import urlshortener.repository.impl.SystemInfoRepositoryImpl;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;
import static urlshortener.fixtures.ShortURLFixture.urlSponsor;

public class SystemInfoRepositoryTests {
    private EmbeddedDatabase db;
    private SystemInfoRepository repository;
    private JdbcTemplate jdbc;

    @Before
    public void setup() {
        db = new EmbeddedDatabaseBuilder().setType(HSQL)
                .addScript("schema-hsqldb.sql").build();
        jdbc = new JdbcTemplate(db);
        ShortURLRepository shortUrlRepository = new ShortURLRepositoryImpl(jdbc);
        shortUrlRepository.save(ShortURLFixture.url1());
        shortUrlRepository.save(ShortURLFixture.url2());
        repository = new SystemInfoRepositoryImpl(jdbc);
    }

    @Test
    public void thatGetInfoReturnsCorrectInfo(){
        SystemInfo systemInfo = repository.getSystemInfo(null);
        assertNotNull(repository.getSystemInfo(null));
        assertSame(jdbc.queryForObject("select count(*) from click", Long.class),
                systemInfo.getNumClicks());

    }
}
