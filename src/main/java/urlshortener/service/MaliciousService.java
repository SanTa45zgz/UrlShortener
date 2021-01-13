package urlshortener.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import urlshortener.domain.Match;
import urlshortener.domain.MatchList;
import urlshortener.domain.SafeBrowsingRequestBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class MaliciousService {

    private final String apiKey = "AIzaSyA40LYA0tac0D6hXyLlFzJv7GyI-qZwvr0";
    private final String safeBrowsingUrl = "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=" + apiKey;

    private final RestTemplate restTemplate;

    public MaliciousService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * Checks if the URL is safe
     * @param url URL to check
     * @return a list of matches that represent the unsafeness of the URL evaluated
     */
    public List<Match> checkUrl(String url) {
        // Transform alone url into list
        List<String> urls = new ArrayList<>();
        urls.add(url);
        // Return request function result
        return sendRequest(urls);
    }

    /**
     * Checks if the URL in the list are safe
     * @param urls list of URLs to check
     * @return a list of matches for URLs passed
     */
    public List<Match> checkUrl(List<String> urls) {
        // Return request function result
        return sendRequest(urls);
    }

    /**
     * Perform a request to the Google Safe Browsing API to check the safeness of the URLs received
     * @param urls list of URLs to check
     * @return a list od matches for the URLs passed
     */
    private List<Match> sendRequest(List<String> urls) {
        // Create headers
        HttpHeaders headers = new HttpHeaders();
        // Set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Create a map for post parameters
        SafeBrowsingRequestBody safeBrowsingRequestBody = new SafeBrowsingRequestBody();
        safeBrowsingRequestBody.setThreatEntries(urls);

        // Build the request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(safeBrowsingRequestBody.createRequestBody(), headers);

        // Send POST request
        ResponseEntity<MatchList> response = this.restTemplate.postForEntity(safeBrowsingUrl, entity, MatchList.class);

        // Check response status code
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().getMatches() != null) {
            // Return the list with matches
            return response.getBody().getMatches();
        } else {
            // Return an empty list
            return new ArrayList<>();
        }
    }
}
