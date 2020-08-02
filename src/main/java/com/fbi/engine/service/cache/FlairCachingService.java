package com.fbi.engine.service.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.query.Query;
import com.fbi.engine.service.grpc.ManagedChannelFactory;
import com.flair.bi.messages.CacheServiceGrpc;
import com.flair.bi.messages.GetCacheResponse;
import com.project.bi.query.FlairQuery;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlairCachingService {

    private final ManagedChannelFactory cacheChannelFactory;
    private volatile CacheServiceGrpc.CacheServiceBlockingStub cacheServiceBlockingStub;
    private volatile ManagedChannel channel;

    private CacheServiceGrpc.CacheServiceBlockingStub getCacheServiceStub() {
        if (cacheServiceBlockingStub == null || (channel != null && channel.isShutdown())) {
            synchronized (this) {
                if (cacheServiceBlockingStub == null || (channel != null && channel.isShutdown())) {
                    cacheServiceBlockingStub = CacheServiceGrpc.newBlockingStub(getChannel());
                }
            }
        }
        return cacheServiceBlockingStub;
    }

    private ManagedChannel getChannel() {
        if (channel == null || channel.isShutdown()) {
            synchronized (this) {
                if (channel == null || channel.isShutdown()) {
                    channel = cacheChannelFactory.getInstance();
                }
            }
        }
        return channel;
    }

    public Optional<CacheMetadata> getResult(String query, String connectionLinkId) {
        log.info("Making a grpc caching request for connection {} query {}",
                connectionLinkId, query);

        GetCacheResponse cache;
        try {
            cache = getCacheServiceStub().getCache(com.flair.bi.messages.GetCacheRequest.newBuilder()
                    .setKey(query)
                    .setTable(connectionLinkId)
                    .build());
        } catch(StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.NOT_FOUND.getCode()) {
                log.info("No cache found for {}", query);
                return Optional.empty();
            }
            log.error("Error fetching data from cache " + e.getMessage() + " status " + e.getStatus() + " trailers " + e.getTrailers(), e);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error fetching data from cache " + e.getMessage() + "", e);
            return Optional.empty();
        }

        CacheMetadata cacheMetadata = new CacheMetadata();
        cacheMetadata.setInterpretedQuery(query);
        cacheMetadata.setDateCreated(Instant.ofEpochSecond(cache.getMetadata().getDateCreated()));
        cacheMetadata.setResult(cache.getResult());
        cacheMetadata.setStale(cache.getMetadata().getStale());
        return Optional.of(cacheMetadata);
    }

    @Async
    public void putResultAsync(String query, String connectionLinkId, String result, CacheParams cacheParams) {
        putResult(query, connectionLinkId, result, cacheParams);
    }

    public void putResult(String query, String connectionLinkId, String result, CacheParams cacheParams) {
        log.info("Putting grpc result into a cache request for connection {} query {} cacheParams {}",
                connectionLinkId, query, cacheParams);

        try {
            getCacheServiceStub().putCache(com.flair.bi.messages.PutCacheRequest.newBuilder()
                    .setKey(query)
                    .setTable(connectionLinkId)
                    .setValue(result)
                    .setRefreshAfterDate(Instant.now().plus(cacheParams.getRefreshAfterMinutes(), ChronoUnit.MINUTES).getEpochSecond())
                    .setRefreshAfterCount(cacheParams.getRefreshAfterTimesRead())
                    .setPurgeAfterDate(Instant.now().plus(cacheParams.getCachePurgeAfterMinutes(), ChronoUnit.MINUTES).getEpochSecond())
                    .build());
        } catch(StatusRuntimeException e) {
            log.error("Error saving data to cache " + e.getMessage() + " status " + e.getStatus() + " trailers " + e.getTrailers(), e);
        } catch (Exception e) {
            log.error("Error saving data to cache " + e.getMessage() + "", e);
        }
    }

}
