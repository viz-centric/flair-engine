package com.fbi.engine.config;

import com.fbi.engine.service.grpc.ManagedChannelFactory;
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
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientGrpcConfig {

    private final EurekaClient client;
    private final GrpcProperties properties;

    @Bean(name = "cacheChannelFactory")
    public ManagedChannelFactory cacheChannelFactory() {
        return createManagedChannelFactory(
                "flair-cache",
                properties.getTls().isEnabled(),
                properties.getTls().getCacheTrustCertCollectionFile(),
                properties.getTls().getCacheClientCertChainFile(),
                properties.getTls().getCacheClientPrivateKeyFile()
        );
    }

    private ManagedChannelFactory createManagedChannelFactory(String serviceName,
                                                              boolean tlsEnabled,
                                                              String trustCertCollectionFile,
                                                              String clientCertChainFile,
                                                              String clientPrivateKeyFile) {
        Supplier<ManagedChannel> dynamicManagedChannel = () -> {

            final InstanceInfo instanceInfo = client.getNextServerFromEureka(serviceName, false);

            NettyChannelBuilder nettyChannelBuilder = NettyChannelBuilder.forAddress(
                    tlsEnabled ? instanceInfo.getHostName() : instanceInfo.getIPAddr(),
                    instanceInfo.getPort());

            log.info("GRPC config: Hostname {} IP {} port {} secure port {} secure vip {}",
                    instanceInfo.getHostName(), instanceInfo.getIPAddr(), instanceInfo.getPort(), instanceInfo.getSecurePort(),
                    instanceInfo.getSecureVipAddress());

            if (tlsEnabled) {

                nettyChannelBuilder.negotiationType(NegotiationType.TLS);

                log.info("GRPC config: GRPC TLS enabled");

                try {
                    nettyChannelBuilder.sslContext(buildSslContext(
                            trustCertCollectionFile,
                            clientCertChainFile,
                            clientPrivateKeyFile
                    ));
                } catch (SSLException e) {
                    log.error("GRPC config: error", e);
                }
            } else {
                nettyChannelBuilder.usePlaintext();
            }
            return nettyChannelBuilder.build();
        };
        return new ManagedChannelFactory(dynamicManagedChannel);
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
