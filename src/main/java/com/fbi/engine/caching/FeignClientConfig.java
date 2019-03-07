package com.fbi.engine.caching;

import feign.Contract;
import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class FeignClientConfig {

    @Bean
    public Contract feignContract() {
        return new Contract.Default();
    }

    @Bean
    public Logger.Level level() {
        return Logger.Level.NONE;
    }

    @Bean
    public Request.Options options() {
        return new Request.Options(10_000, 60_000);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ErrorDecoder.Default();
    }

}
