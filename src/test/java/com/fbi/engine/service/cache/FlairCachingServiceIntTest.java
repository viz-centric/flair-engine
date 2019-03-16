package com.fbi.engine.service.cache;

import com.fbi.engine.AbstractGrpcTest;
import com.fbi.engine.FbiengineApp;
import com.fbi.engine.service.ManagedGrpcFactoryService;
import com.flair.bi.messages.CacheServiceGrpc;
import com.flair.bi.messages.GetCacheRequest;
import com.flair.bi.messages.GetCacheResponse;
import com.netflix.discovery.EurekaClient;
import com.project.bi.query.FlairQuery;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class FlairCachingServiceIntTest extends AbstractGrpcTest {

    @Autowired
    private FlairCachingService flairCachingService;

    @MockBean
    private ManagedGrpcFactoryService managedGrpcFactoryService;

    @Mock
    private CacheServiceGrpc.CacheServiceImplBase cacheService;

    private ManagedChannel channel;

    @Before
    public void setUp() throws Exception {
        String serverName = InProcessServerBuilder.generateName();

        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName)
                .directExecutor()
                .addService(cacheService)
                .build()
                .start());

        channel = grpcCleanup.register(InProcessChannelBuilder
                .forName(serverName)
                .directExecutor()
                .build());

        when(managedGrpcFactoryService.getManagedChannel(eq("flair-cache"))).thenReturn(channel);
    }

    @Test
    public void getResultReturnsCachedValue() {
        long dateCreated = Instant.now().getEpochSecond();

        doAnswer(invocationOnMock -> {
            GetCacheRequest getCacheRequest = invocationOnMock.getArgumentAt(0, GetCacheRequest.class);
            StreamObserver<GetCacheResponse> streamObserver = invocationOnMock.getArgumentAt(1, StreamObserver.class);
            streamObserver.onNext(GetCacheResponse.newBuilder()
                    .setResult("test")
                    .setMetadata(com.flair.bi.messages.CacheMetadata.newBuilder()
                            .setStale(true)
                            .setDateCreated(dateCreated)
                            .build())
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
        long dateCreated = Instant.now().getEpochSecond();

        doAnswer(invocationOnMock -> {
            GetCacheRequest getCacheRequest = invocationOnMock.getArgumentAt(0, GetCacheRequest.class);
            StreamObserver<GetCacheResponse> streamObserver = invocationOnMock.getArgumentAt(1, StreamObserver.class);
            streamObserver.onError(Status.NOT_FOUND
                    .withDescription("errors.cache.not_found")
                    .asRuntimeException());
            streamObserver.onCompleted();
            return null;
        }).when(cacheService).getCache(any(GetCacheRequest.class), any(StreamObserver.class));

        Optional<CacheMetadata> result = flairCachingService.getResult(new FlairQuery("select 1", false), "connectId");
        assertFalse(result.isPresent());
    }
}