package com.fbi.engine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties("flair-cache")
public class FlairCachingConfig {

    private boolean enabled;
    private String url;

}
