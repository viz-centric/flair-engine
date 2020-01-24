package com.fbi.engine.service.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fbi.engine.AbstractGrpcTest;
import com.fbi.engine.service.grpc.ManagedChannelFactory;
import com.flair.bi.messages.CacheServiceGrpc;
import com.flair.bi.messages.GetCacheRequest;
import com.flair.bi.messages.GetCacheResponse;
import com.flair.bi.messages.PutCacheRequest;
import com.flair.bi.messages.PutCacheResponse;
import com.project.bi.query.FlairQuery;

import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;

public class FlairCachingServiceIntTest extends AbstractGrpcTest {

    @Autowired
    private FlairCachingService flairCachingService;

    @MockBean
    private ManagedChannelFactory managedChannelFactory;

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

        when(managedChannelFactory.getInstance()).thenReturn(channel);
    }

    @Test
    public void getResultReturnsCachedValue() {
        long dateCreated = Instant.now().getEpochSecond();

        doAnswer(invocationOnMock -> {
            GetCacheRequest getCacheRequest = invocationOnMock.getArgument(0, GetCacheRequest.class);
            StreamObserver<GetCacheResponse> streamObserver = invocationOnMock.getArgument(1, StreamObserver.class);
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
        doAnswer(invocationOnMock -> {
            GetCacheRequest getCacheRequest = invocationOnMock.getArgument(0, GetCacheRequest.class);
            StreamObserver<GetCacheResponse> streamObserver = invocationOnMock.getArgument(1, StreamObserver.class);
            streamObserver.onError(Status.NOT_FOUND
                    .withDescription("errors.cache.not_found")
                    .asRuntimeException());
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
            PutCacheRequest getCacheRequest = invocationOnMock.getArgument(0, PutCacheRequest.class);
            StreamObserver<PutCacheResponse> streamObserver = invocationOnMock.getArgument(1, StreamObserver.class);
            assertEquals("result", getCacheRequest.getValue());
            streamObserver.onNext(PutCacheResponse.newBuilder().build());
            streamObserver.onCompleted();
            return null;
        }).when(cacheService).putCache(any(PutCacheRequest.class), any(StreamObserver.class));

        flairCachingService.putResult(new FlairQuery("select 1", false), "connectId", "result", new CacheParams());

        assertTrue(cacheSaved.get());
    }
}
