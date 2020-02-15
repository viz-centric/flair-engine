package com.fbi.engine;

import java.io.IOException;
import java.util.Optional;

import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.lognet.springboot.grpc.GRpcServerRunner;
import org.lognet.springboot.grpc.autoconfigure.GRpcServerProperties;
import org.lognet.springboot.grpc.context.LocalRunningGrpcPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.testing.GrpcCleanupRule;

@Profile("grpc")
@SpringBootTest(classes = FbiengineApp.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
public abstract class AbstractGrpcTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGrpcTest.class);

	@Rule
	public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

	@Autowired(required = false)
	@Qualifier("grpcServerRunner")
	protected GRpcServerRunner grpcServerRunner;

	@Autowired(required = false)
	@Qualifier("grpcInprocessServerRunner")
	protected GRpcServerRunner grpcInprocessServerRunner;

	protected ManagedChannel channel;
	protected ManagedChannel inProcChannel;

	@LocalRunningGrpcPort
	protected int runningPort;

	@Autowired
	protected ApplicationContext context;

	@Autowired
	protected GRpcServerProperties gRpcServerProperties;

	@BeforeEach
	public final void setupChannels() throws IOException {
		LOGGER.info("Grpc properties enabled: {}", gRpcServerProperties.isEnabled());
		if (gRpcServerProperties.isEnabled()) {
			ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress("localhost", getPort());
			Resource certChain = Optional.ofNullable(gRpcServerProperties.getSecurity())
					.map(GRpcServerProperties.SecurityProperties::getCertChain).orElse(null);
			if (null != certChain) {
				((NettyChannelBuilder) channelBuilder).useTransportSecurity()
						.sslContext(GrpcSslContexts.forClient().trustManager(certChain.getInputStream()).build());
			} else {
				channelBuilder.usePlaintext();
			}

			channel = onChannelBuild(channelBuilder).build();
		}
		if (StringUtils.hasText(gRpcServerProperties.getInProcessServerName())) {
			inProcChannel = onChannelBuild(
					InProcessChannelBuilder.forName(gRpcServerProperties.getInProcessServerName()).usePlaintext())
							.build();

		}
	}

	@AfterEach
	public final void shutdownChannels() {
		Optional.ofNullable(channel).ifPresent(ManagedChannel::shutdownNow);
		Optional.ofNullable(inProcChannel).ifPresent(ManagedChannel::shutdownNow);
	}

	protected int getPort() {
		return runningPort;
	}

	protected ManagedChannelBuilder<?> onChannelBuild(ManagedChannelBuilder<?> channelBuilder) {
		return channelBuilder;
	}

	protected InProcessChannelBuilder onChannelBuild(InProcessChannelBuilder channelBuilder) {
		return channelBuilder;
	}

}
