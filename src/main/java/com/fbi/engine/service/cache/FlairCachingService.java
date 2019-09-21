package com.fbi.engine.service.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper = new ObjectMapper();
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

    public Optional<CacheMetadata> getResult(FlairQuery query, String connectionLinkId) {
        log.info("Making a grpc caching request for connection {} query {}",
                connectionLinkId, query);

        Optional<String> cacheKey = getCacheKey(query);

        if (!cacheKey.isPresent()) {
            log.error("Error creating a cache key for query " + query + " and connection " + connectionLinkId);
            return Optional.empty();
        }

        GetCacheResponse cache;
        try {
            cache = getCacheServiceStub().getCache(com.flair.bi.messages.GetCacheRequest.newBuilder()
                    .setKey(cacheKey.get())
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
        cacheMetadata.setDateCreated(Instant.ofEpochSecond(cache.getMetadata().getDateCreated()));
        cacheMetadata.setResult(cache.getResult());
        cacheMetadata.setStale(cache.getMetadata().getStale());
        return Optional.of(cacheMetadata);
    }

    private Optional<String> getCacheKey(FlairQuery query) {
        try {
            return Optional.of(objectMapper.writeValueAsString(query));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    @Async
    public void putResultAsync(FlairQuery query, String connectionLinkId, String result, CacheParams cacheParams) {
        putResult(query, connectionLinkId, result, cacheParams);
    }

    public void putResult(FlairQuery query, String connectionLinkId, String result, CacheParams cacheParams) {
        log.info("Putting grpc result into a cache request for connection {} query {} result {} cacheParams {}",
                connectionLinkId, query.getStatement(), result, cacheParams);

        Optional<String> cacheKey = getCacheKey(query);

        if (!cacheKey.isPresent()) {
            log.error("Error creating a cache key for query " + query + " and connection " + connectionLinkId);
            return;
        }

        try {
            getCacheServiceStub().putCache(com.flair.bi.messages.PutCacheRequest.newBuilder()
                    .setKey(cacheKey.get())
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
