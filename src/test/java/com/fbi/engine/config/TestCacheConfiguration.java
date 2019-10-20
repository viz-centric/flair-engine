package com.fbi.engine.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@Slf4j
@RequiredArgsConstructor
@AutoConfigureBefore(value = { DatabaseConfiguration.class })
public class TestCacheConfiguration {

    @Bean
    public CacheManager cacheManager() {
        log.info("Starting SimpleCacheManager");
        return new SimpleCacheManager();
    }

}
