package com.fbi.engine.config;

import java.io.File;

import org.lognet.springboot.grpc.GRpcServerBuilderConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import com.fbi.engine.ApplicationProperties;

import io.grpc.ServerBuilder;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by reddys on 02/07/2018.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "grpc.enabled", havingValue = "true")
public class GrpcConfig extends GRpcServerBuilderConfigurer {

	@Autowired
	private ApplicationProperties properties;

	@Override
	public void configure(final ServerBuilder<?> serverBuilder) {
		final GrpcServerProperties grpcProperties = properties.getGrpc();
		log.info("Grpc config: Configuring grpc {}", grpcProperties.getTls());

		if (grpcProperties.getTls().isEnabled()) {
			final NettyServerBuilder nsb = (NettyServerBuilder) serverBuilder;
			try {
				nsb.sslContext(getSslContextBuilder().build());
			} catch (final Exception e) {
				log.error("Grpc config: Error configuring ssl", e);
			}
		}
	}

	private SslContextBuilder getSslContextBuilder() {
		final GrpcServerProperties grpcProperties = properties.getGrpc();
		log.info("Grpc config: Configuring ssl cert {} key {} trust {}", grpcProperties.getTls().getCertChainFile(),
				grpcProperties.getTls().getPrivateKeyFile(), grpcProperties.getTls().getTrustCertCollectionFile());

		final SslContextBuilder sslClientContextBuilder = SslContextBuilder.forServer(
				new File(grpcProperties.getTls().getCertChainFile()),
				new File(grpcProperties.getTls().getPrivateKeyFile()));

		if (grpcProperties.getTls().getTrustCertCollectionFile() != null) {
			sslClientContextBuilder.trustManager(new File(grpcProperties.getTls().getTrustCertCollectionFile()));
			sslClientContextBuilder.clientAuth(ClientAuth.REQUIRE);
		}
		return GrpcSslContexts.configure(sslClientContextBuilder, SslProvider.OPENSSL);
	}
}
