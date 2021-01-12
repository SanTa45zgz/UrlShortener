package urlshortener.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Class for periodic memory cache clearing
 */
@Component
public class ClearCache {

    private static final Logger log = LoggerFactory
            .getLogger(ClearCache.class);

    @Autowired
    private CacheManager cacheManager;

    /**
     * Clear the cache every 1 hour
     */
    @Scheduled(initialDelay = 3600000, fixedRate = 3600000)
    public void reportCurrentTime() {
        cacheManager.getCacheNames().parallelStream().forEach(name -> cacheManager.getCache(name).clear());
        log.debug("Cache Flushed!");
    }
}
