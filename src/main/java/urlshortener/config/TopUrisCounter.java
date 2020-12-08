package urlshortener.config;

import io.micrometer.core.instrument.MeterRegistry;
import com.sun.tools.javac.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import urlshortener.repository.SystemInfoRepository;

import java.util.List;

@Component
public class TopUrisCounter {
    private final MeterRegistry meterRegistry;
    private final SystemInfoRepository systemInfoRepository;


    public TopUrisCounter(MeterRegistry meterRegistry,
                             SystemInfoRepository systemInfoRepository){
        this.meterRegistry = meterRegistry;
        this.systemInfoRepository = systemInfoRepository;
    }


    @Scheduled(fixedDelay = 1000)
    public void http_redirects(){
        List<Pair<String, Long>> topUris = getRankingUris();
        for (Pair<String, Long> item : topUris) {
            meterRegistry.counter("http_redirects", "hash", item.fst, "value", item.snd.toString()).increment();
        }
    }

    private List<Pair<String, Long>> getRankingUris(){
        return systemInfoRepository.getTopUris();
    }

}
