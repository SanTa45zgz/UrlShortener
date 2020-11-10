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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
public class SystemInfoTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testInfoModifiedAfterClickingLink() throws Exception{
        ResponseEntity<String> shortUriResponse = postLink("http://example.com");
        assertThat(shortUriResponse.getStatusCode(), is(HttpStatus.CREATED));
        ReadContext createdShortUri = JsonPath.parse(shortUriResponse.getBody());

        ResponseEntity<String> sysInfoEntity = restTemplate.getForEntity("/stats", String.class);
        assertThat(shortUriResponse.getStatusCode(), is(HttpStatus.OK));
        ReadContext initialInfo = JsonPath.parse(sysInfoEntity.getBody());
    }

    private ResponseEntity<String> postLink(String url) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("url", url);
        return restTemplate.postForEntity("/link", parts, String.class);
    }
}
