package com.fbi.engine.service;

import com.fbi.engine.config.GrpcProperties;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLException;
import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagedGrpcFactoryService {

    private final EurekaClient client;
    private final GrpcProperties properties;

    public ManagedChannel getManagedChannel(String eurekaName) {
        final InstanceInfo instanceInfo = client.getNextServerFromEureka(eurekaName, false);

        NettyChannelBuilder nettyChannelBuilder = NettyChannelBuilder.forAddress(
                properties.getTls().isEnabled() ? instanceInfo.getHostName() : instanceInfo.getIPAddr(),
                instanceInfo.getPort());

        log.info("GRPC config: Hostname {} IP {} port {} secure port {} secure vip {}",
                instanceInfo.getHostName(), instanceInfo.getIPAddr(), instanceInfo.getPort(), instanceInfo.getSecurePort(),
                instanceInfo.getSecureVipAddress());

        if (properties.getTls().isEnabled()) {

            nettyChannelBuilder.negotiationType(NegotiationType.TLS);

            log.info("GRPC config: GRPC TLS enabled");

            try {
                nettyChannelBuilder.sslContext(buildSslContext(
                        properties.getTls().getCacheTrustCertCollectionFile(),
                        properties.getTls().getCacheClientCertChainFile(),
                        properties.getTls().getCacheClientPrivateKeyFile()
                ));
            } catch (SSLException e) {
                log.error("GRPC config: error", e);
            }
        } else {
            nettyChannelBuilder.usePlaintext();
        }
        return nettyChannelBuilder.build();
    }

    private static SslContext buildSslContext(String trustCertCollectionFilePath,
                                              String clientCertChainFilePath,
                                              String clientPrivateKeyFilePath) throws SSLException {
        SslContextBuilder builder = GrpcSslContexts.forClient();
        if (trustCertCollectionFilePath != null) {
            builder.trustManager(new File(trustCertCollectionFilePath));
        }
        if (clientCertChainFilePath != null && clientPrivateKeyFilePath != null) {
            builder.keyManager(new File(clientCertChainFilePath), new File(clientPrivateKeyFilePath));
        }
        return builder.build();
    }
}
