package urlshortener.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.broker.BrokerClient;
import urlshortener.domain.ShortURL;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static urlshortener.fixtures.GeoLocationFixture.someGeoLocation;
import static urlshortener.fixtures.ShortURLFixture.someUrl;

public class RedirectTests {

    private MockMvc mockMvc;

    @Mock
    private ShortURLService shortUrlService;

    @Mock
    private ClickService clickService;

    @Mock
    private BrokerClient brokerClient;

    @InjectMocks
    private RedirectController redirect;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(redirect).build();
    }

    @Test
    public void thatRedirectToReturnsTemporaryRedirectIfKeyExists()
            throws Exception {
        when(shortUrlService.findByKey("someKey")).thenReturn(someUrl());
        when(brokerClient.getLocationFromIp("127.0.0.1")).thenReturn(someGeoLocation());

        mockMvc.perform(get("/{id}", "someKey")).andDo(print())
                .andExpect(status().isTemporaryRedirect())
                .andExpect(redirectedUrl("http://example.com/"));
    }

    @Test
    public void thatRedirectToReturnsNotFoundIdIfKeyDoesNotExist()
            throws Exception {
        when(shortUrlService.findByKey("someKey")).thenReturn(null);

        mockMvc.perform(get("/{id}", "someKey")).andDo(print())
                .andExpect(status().isNotFound());
    }

    //NEW TEST

    /**
     * Check that a link with sponsor redirects correctly to the advertising URL
     *
     * @throws Exception
     */
    @Test
    public void thatRedirectToReturnsTemporaryRedirectToAdIfKeyExists()
            throws Exception {
        ShortURL l = new ShortURL("unizar", "http://unizar.es/", null, "yes", null,
                null, 307, true, null, null);
        when(shortUrlService.findByKey("unizar")).thenReturn(l);
        when(brokerClient.getLocationFromIp("127.0.0.1")).thenReturn(someGeoLocation());

        mockMvc.perform(get("/{id}", "unizar"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("ad/unizar"));
    }

    /**
     * Check that it returns the destination URL of a shortened link
     *
     * @throws Exception
     */
    @Test
    public void getDestinationUrl()
            throws Exception {
        ShortURL l = new ShortURL("unizar", "http://unizar.es/", null, "yes", null,
                null, 307, true, null, null);
        when(shortUrlService.findByKey("unizar")).thenReturn(l);

        mockMvc.perform(get("/redirect").param("hash", "unizar")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("http://unizar.es/"));
    }

}
