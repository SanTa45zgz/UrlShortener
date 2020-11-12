package urlshortener.integration;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


import static org.hamcrest.Matchers.not;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
public class SystemInfoTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testSystemInfoChangesAfterClickingLink() throws Exception{
        ResponseEntity<String> shortUriResponse = postLink("http://example.com");
        assertThat(shortUriResponse.getStatusCode(), is(HttpStatus.CREATED));
        ReadContext createdShortUri = JsonPath.parse(shortUriResponse.getBody());

        ResponseEntity<String> sysInfoEntity = restTemplate.getForEntity("/stats", String.class);
        assertThat(sysInfoEntity.getStatusCode(), is(HttpStatus.OK));
        ReadContext initialInfo = JsonPath.parse(sysInfoEntity.getBody());

        shortUriResponse = restTemplate.getForEntity("/"+ createdShortUri
                .read("$.hash"), String.class);
        assertThat(shortUriResponse.getStatusCode(), not(HttpStatus.NOT_FOUND));

        sysInfoEntity = restTemplate.getForEntity("/stats", String.class);
        assertThat(sysInfoEntity.getStatusCode(), is(HttpStatus.OK));
        ReadContext finalInfo = JsonPath.parse(sysInfoEntity.getBody());

        assertEquals((int)finalInfo.read("$.numUsers"),
                (int)initialInfo.read("$.numUsers") + 1);

        assertEquals((int)finalInfo.read("$.numClicks"),
                (int)initialInfo.read("$.numClicks") + 1);
    }

    @Test
    public void testSystemInfoChangesAfterCreatingLink(){
        ResponseEntity<String> sysInfoEntity = restTemplate.getForEntity("/stats", String.class);
        assertThat(sysInfoEntity.getStatusCode(), is(HttpStatus.OK));
        ReadContext initialInfo = JsonPath.parse(sysInfoEntity.getBody());

        ResponseEntity<String> shortUriResponse = postLink("http://example2.com");
        assertThat(shortUriResponse.getStatusCode(), is(HttpStatus.CREATED));

        sysInfoEntity = restTemplate.getForEntity("/stats", String.class);
        ReadContext finalInfo = JsonPath.parse(sysInfoEntity.getBody());

        assertEquals((int)finalInfo.read("$.numUris"),
                (int)initialInfo.read("$.numUris") + 1);
    }

    private ResponseEntity<String> postLink(String url) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("url", url);
        return restTemplate.postForEntity("/link", parts, String.class);
    }
}
