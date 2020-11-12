package urlshortener.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import urlshortener.domain.GeoLocation;
import urlshortener.fixtures.SystemInfoFixture;
import urlshortener.repository.SystemInfoRepository;
import urlshortener.service.GeoLocationService;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class SystemInfoControllerTests {
    private MockMvc mockMvc;

    @Mock
    private SystemInfoRepository systemInfoRepository;
    private GeoLocationService geoLocationService;

    @InjectMocks
    private SystemInfoController systemInfoController;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        this.mockMvc= MockMvcBuilders.standaloneSetup(systemInfoController).build();

    }

    @Test
    public void thatSystemInfoResponseIsOk()
        throws Exception {
        when(systemInfoRepository.getSystemInfo(null))
                .thenReturn(SystemInfoFixture.systemInfo());

        mockMvc.perform(get("/stats"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.numUsers", is(1))))
                .andExpect((jsonPath("$.numClicks", is(1))))
                .andExpect((jsonPath("$.numUris", is(1))));}
}
