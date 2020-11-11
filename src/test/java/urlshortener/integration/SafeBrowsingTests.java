package urlshortener.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import urlshortener.domain.Match;
import urlshortener.service.MaliciousService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
public class SafeBrowsingTests {

    @Autowired
    private MaliciousService maliciousService;

    @Test
    public void testSafeBrowsing() {
        String url = "https://www.google.es";
        List<Match> result = maliciousService.checkUrl(url);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testMultipleSafeBrowsing() {
        List<String> urls = new ArrayList<>();
        urls.add("https://www.google.es");
        urls.add("https://testsafebrowsing.appspot.com/s/phishing.html");
        List<Match> result = maliciousService.checkUrl(urls);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        List<String> threats = new ArrayList<>();
        for (Match match : result) {
            threats.addAll(match.getThreat().values());
        }
        assertTrue(urls.containsAll(threats));
    }
}
