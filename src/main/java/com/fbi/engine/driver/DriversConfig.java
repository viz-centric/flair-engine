package com.fbi.engine.driver;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "app.drivers", ignoreUnknownFields = false)
@Component
@Data
public class DriversConfig {
    private String dir;
    private List<String> names;
}
