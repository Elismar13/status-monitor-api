package com.thushima.statusmonitor.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${app.cache.status.ttl:30}")
    private int statusCacheTtl;

    @Value("${app.cache.status.max-size:1000}")
    private int statusCacheMaxSize;

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                .expireAfterWrite(statusCacheTtl, TimeUnit.SECONDS)
                .maximumSize(statusCacheMaxSize)
                .recordStats();
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager("statusCache");
        caffeineCacheManager.setAsyncCacheMode(true);
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }
}
