package com.fbi.engine.service.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lognet.springboot.grpc.GRpcServerBuilderConfigurer;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureRule;

import com.fbi.engine.AbstractGrpcTest;
import com.fbi.engine.FbiengineApp;
import com.fbi.engine.TestConfig;
import com.fbi.engine.service.grpc.ManagedChannelFactory;
import com.flair.bi.messages.CacheServiceGrpc;
import com.flair.bi.messages.GetCacheRequest;
import com.flair.bi.messages.GetCacheResponse;
import com.flair.bi.messages.PutCacheRequest;
import com.flair.bi.messages.PutCacheResponse;
import com.project.bi.query.FlairQuery;

import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;

@SpringBootTest(classes = { FbiengineApp.class,
		TestConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT, properties = { "grpc.enabled=true",
				"grpc.inProcessServerName=testing", "grpc.enableReflection=true", "grpc.port=0",
				"grpc.shutdownGrace=-1" })
public class FlairCachingServiceIntTest extends AbstractGrpcTest {

	@Autowired
	private FlairCachingService flairCachingService;

	@MockBean
	private ManagedChannelFactory managedChannelFactory;

	@Mock
	private CacheServiceGrpc.CacheServiceImplBase cacheService;

	@Autowired
	private GRpcServerBuilderConfigurer configurer;

	@Rule
	public OutputCaptureRule outputCapture = new OutputCaptureRule();

	@Autowired
	@Qualifier("globalInterceptor")
	private ServerInterceptor globalInterceptor;

	@BeforeEach
	public void setUp() throws Exception {
		configurer.configure(InProcessServerBuilder.forName("testing").directExecutor().addService(cacheService));
		when(managedChannelFactory.getInstance()).thenReturn(inProcChannel);

		String serverName = InProcessServerBuilder.generateName();

		grpcCleanup.register(
				InProcessServerBuilder.forName(serverName).directExecutor().addService(cacheService).build().start());

		inProcChannel = grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());

		when(managedChannelFactory.getInstance()).thenReturn(inProcChannel);
	}

	@Test
	public void getResultReturnsCachedValue() {
		long dateCreated = Instant.now().getEpochSecond();

		doAnswer(invocationOnMock -> {
			StreamObserver<GetCacheResponse> streamObserver = invocationOnMock.getArgument(1);
			streamObserver.onNext(GetCacheResponse.newBuilder().setResult("test").setMetadata(
					com.flair.bi.messages.CacheMetadata.newBuilder().setStale(true).setDateCreated(dateCreated).build())
					.build());
			streamObserver.onCompleted();
			return null;
		}).when(cacheService).getCache(any(GetCacheRequest.class), any(StreamObserver.class));

		Optional<CacheMetadata> result = flairCachingService.getResult(new FlairQuery("select 1", false), "connectId");
		assertEquals("test", result.get().getResult());
		assertEquals(dateCreated, result.get().getDateCreated().getEpochSecond());
		assertTrue(result.get().isStale());
	}

	@Test
	public void getResultReturnsCacheNotFound() {
		doAnswer(invocationOnMock -> {
			StreamObserver<GetCacheResponse> streamObserver = invocationOnMock.getArgument(1);
			streamObserver.onError(Status.NOT_FOUND.withDescription("errors.cache.not_found").asRuntimeException());
			streamObserver.onCompleted();
			return null;
		}).when(cacheService).getCache(any(GetCacheRequest.class), any(StreamObserver.class));

		Optional<CacheMetadata> result = flairCachingService.getResult(new FlairQuery("select 1", false), "connectId");
		assertFalse(result.isPresent());
	}

	@Test
	public void putResult() {
		AtomicBoolean cacheSaved = new AtomicBoolean(false);
		doAnswer(invocationOnMock -> {
			cacheSaved.set(true);
			PutCacheRequest getCacheRequest = invocationOnMock.getArgument(0);
			StreamObserver<PutCacheResponse> streamObserver = invocationOnMock.getArgument(1);
			assertEquals("result", getCacheRequest.getValue());
			streamObserver.onNext(PutCacheResponse.newBuilder().build());
			streamObserver.onCompleted();
			return null;
		}).when(cacheService).putCache(any(PutCacheRequest.class), any(StreamObserver.class));

		flairCachingService.putResult(new FlairQuery("select 1", false), "connectId", "result", new CacheParams());

		assertTrue(cacheSaved.get());
	}
}
