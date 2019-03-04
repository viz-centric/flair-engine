package com.fbi.engine.config;

import io.grpc.ServerBuilder;
import org.lognet.springboot.grpc.GRpcServerBuilderConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;

/**
 * Created by reddys on 02/07/2018.
 */

@RefreshScope
@Configuration
@Profile("grpc")
public class GrpcConfig extends GRpcServerBuilderConfigurer {

    @Value("${grpc.tls.enabled:false}")
    private boolean enabled;

    @Value("${grpc.tls.certificate:}")
    private String certificate;

    @Value("${grpc.tls.key:}")
    private String key;

    @Override
    public void configure(ServerBuilder<?> serverBuilder) {

        if (enabled) {
            serverBuilder.useTransportSecurity(new File(certificate), new File(key));
        }
    }
}
