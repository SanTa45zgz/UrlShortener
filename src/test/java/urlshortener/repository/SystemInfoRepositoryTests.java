package urlshortener.repository;

import org.junit.Before;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import urlshortener.fixtures.ShortURLFixture;
import urlshortener.repository.impl.ShortURLRepositoryImpl;
import urlshortener.repository.impl.SystemInfoRepositoryImpl;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

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

}
