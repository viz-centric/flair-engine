package com.fbi.engine.config;

import java.io.File;
import java.util.function.Supplier;

import javax.net.ssl.SSLException;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.fbi.engine.ApplicationProperties;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientGrpcConfig {

	private final static String CHANNEL_SERVICE_NAME = "flair-cache";
	private final EurekaClient client;
	private final ApplicationProperties appProperties;
	private final FlairCachingConfig flairCachingConfig;

	@Bean(name = "cacheChannelFactory")
	public ManagedChannelFactory cacheChannelFactory() {
		final GrpcClientProperties properties = appProperties.getFlairCache().getGrpc();
		return createManagedChannelFactory(CHANNEL_SERVICE_NAME, properties.getTls().isEnabled(),
				properties.getTls().getCacheTrustCertCollectionFile(),
				properties.getTls().getCacheClientCertChainFile(), properties.getTls().getCacheClientPrivateKeyFile());
	}

	private ManagedChannelFactory createManagedChannelFactory(final String serviceName, final boolean tlsEnabled,
			final String trustCertCollectionFile, final String clientCertChainFile, final String clientPrivateKeyFile) {
		final Supplier<ManagedChannel> dynamicManagedChannel = () -> {

			final NettyChannelBuilder nettyChannelBuilder = constructBuilder(serviceName, tlsEnabled);

			if (tlsEnabled) {

				nettyChannelBuilder.negotiationType(NegotiationType.TLS);

				log.info("GRPC config: GRPC TLS enabled");

				try {
					nettyChannelBuilder.sslContext(
							buildSslContext(trustCertCollectionFile, clientCertChainFile, clientPrivateKeyFile));
				} catch (final SSLException e) {
					log.error("GRPC config: error", e);
				}
			} else {
				nettyChannelBuilder.usePlaintext();
			}
			return nettyChannelBuilder.build();
		};
		return new ManagedChannelFactory(dynamicManagedChannel);
	}

	private NettyChannelBuilder constructBuilder(String serviceName, boolean tlsEnabled) {
		if (flairCachingConfig.getUrl() == null) {
			log.info("GRPC config: Hostname url {}", flairCachingConfig.getUrl());
			return NettyChannelBuilder.forTarget(flairCachingConfig.getUrl());
		} else {
			final InstanceInfo instanceInfo = client.getNextServerFromEureka(serviceName, false);

			final NettyChannelBuilder nettyChannelBuilder = NettyChannelBuilder.forAddress(
					tlsEnabled ? instanceInfo.getHostName() : instanceInfo.getIPAddr(), instanceInfo.getPort());

			log.info("GRPC config: Hostname {} IP {} port {} secure port {} secure vip {}", instanceInfo.getHostName(),
					instanceInfo.getIPAddr(), instanceInfo.getPort(), instanceInfo.getSecurePort(),
					instanceInfo.getSecureVipAddress());
			return nettyChannelBuilder;
		}
	}

	private static SslContext buildSslContext(final String trustCertCollectionFilePath,
			final String clientCertChainFilePath, final String clientPrivateKeyFilePath) throws SSLException {
		final SslContextBuilder builder = GrpcSslContexts.forClient();
		if (trustCertCollectionFilePath != null) {
			builder.trustManager(new File(trustCertCollectionFilePath));
		}
		if (clientCertChainFilePath != null && clientPrivateKeyFilePath != null) {
			builder.keyManager(new File(clientCertChainFilePath), new File(clientPrivateKeyFilePath));
		}
		return builder.build();
	}
}
