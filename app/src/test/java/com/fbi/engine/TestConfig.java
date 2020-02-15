package com.fbi.engine;

import io.grpc.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.*;

import org.lognet.springboot.grpc.GRpcGlobalInterceptor;
import org.lognet.springboot.grpc.GRpcServerBuilderConfigurer;

@Configuration
public class TestConfig {
	public static final String CUSTOM_EXECUTOR_MESSAGE = "Hello from custom executor.";

	@Bean(name = "globalInterceptor")
	@GRpcGlobalInterceptor
	public ServerInterceptor globalInterceptor() {

		ServerInterceptor mock = mock(ServerInterceptor.class);
		when(mock.interceptCall(isNotNull(), isNotNull(), isNotNull()))
				.thenAnswer(new Answer<ServerCall.Listener<?>>() {
					@SuppressWarnings("unchecked")
					@Override
					public ServerCall.Listener<?> answer(InvocationOnMock invocation) throws Throwable {
						return ServerCallHandler.class.cast(invocation.getArguments()[2]).startCall(
								ServerCall.class.cast(invocation.getArguments()[0]),
								Metadata.class.cast(invocation.getArguments()[1]));
					}
				});
		return mock;
	}

	@Bean
	@Profile("customServerBuilder")
	public GRpcServerBuilderConfigurer customGrpcServerBuilderConfigurer() {
		return new GRpcServerBuilderConfigurer() {
			@Override
			public void configure(ServerBuilder<?> serverBuilder) {
				serverBuilder.executor(command -> {
					System.out.println(CUSTOM_EXECUTOR_MESSAGE);
					command.run();
				});

			}
		};
	}

}
