package com.fbi.engine.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutowireHelperConfiguration {

    @Bean
    public AutowireHelper autowireHelper() {
        return AutowireHelper.getInstance();
    }
}
