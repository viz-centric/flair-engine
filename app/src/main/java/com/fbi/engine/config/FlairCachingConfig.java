package com.fbi.engine.config;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FlairCachingConfig {

    private boolean enabled;

    private final GrpcClientProperties grpc = new GrpcClientProperties();

}
