package com.fbi.engine.service.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flair.bi.messages.CacheServiceGrpc;
import com.flair.bi.messages.GetCacheResponse;
import com.flair.bi.messages.PutCacheResponse;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.project.bi.query.FlairQuery;
import feign.FeignException;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlairCachingService {

    private final FlairCachingFeignClient flairCachingFeignClient;
    private final EurekaClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private CacheServiceGrpc.CacheServiceBlockingStub getCacheServiceStub() {
        final InstanceInfo instanceInfo = client.getNextServerFromEureka("flair-cache", false);
        final ManagedChannel channel = ManagedChannelBuilder.forAddress(instanceInfo.getIPAddr(), instanceInfo.getPort())
                .usePlaintext()
                .build();
       return CacheServiceGrpc.newBlockingStub(channel);
    }

    public Optional<CacheMetadata> getResultGrpc(FlairQuery query, String connectionLinkId) {
        log.info("Making a grpc caching request for connection {} query {}",
                connectionLinkId, query);

        String cacheKey;
        try {
            cacheKey = objectMapper.writeValueAsString(query);
        } catch (JsonProcessingException e) {
            log.error("Error creating a cache key for query " + query + " and connection " + connectionLinkId);
            return Optional.empty();
        }

        GetCacheResponse cache;
        try {
            cache = getCacheServiceStub().getCache(com.flair.bi.messages.GetCacheRequest.newBuilder()
                    .setKey(cacheKey)
                    .setTable(connectionLinkId)
                    .build());
        } catch(StatusRuntimeException e) {
            if (e.getStatus() == Status.NOT_FOUND) {
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
        return Optional.of(cacheMetadata);
    }

    public Optional<CacheMetadata> getResult(FlairQuery query, String connectionLinkId) {
        log.info("Making a caching request for connection {} query {}",
                connectionLinkId, query);

        String cacheKey;
        try {
            cacheKey = objectMapper.writeValueAsString(query);
        } catch (JsonProcessingException e) {
            log.error("Error creating a cache key for query " + query + " and connection " + connectionLinkId);
            return Optional.empty();
        }

        try {
            CacheResultResponse cacheResultResponse = flairCachingFeignClient
                    .getCache(new GetCacheRequest(cacheKey, connectionLinkId));

            log.debug("Cache meta {} for query {}", cacheResultResponse, query);

            return Optional.ofNullable(cacheResultResponse.getCache());
        } catch (FeignException e) {
            if (e.status() == HttpStatus.NOT_FOUND.value()) {
                log.info("No cache found for {}", query);
            } else {
                log.error("Error fetching data from cache " + e.getMessage() + " status " + e.status());
            }
        }

        return Optional.empty();
    }

    @Async
    public void putResult(FlairQuery query, String connectionLinkId, String result) {
        log.info("Putting result into a cache request for connection {} query {} result {}",
                connectionLinkId, query, result);

        String cacheKey;
        try {
            cacheKey = objectMapper.writeValueAsString(query);
        } catch (JsonProcessingException e) {
            log.error("Error creating a cache key for query " + query + " and connection " + connectionLinkId);
            return;
        }

        try {
            flairCachingFeignClient.putCache(new PutCacheRequest(cacheKey, connectionLinkId, result));
        } catch (FeignException e) {
            log.error("Error putting data to cache " + e.getMessage() + " status " + e.status());
        }
    }

    @Async
    public void putResultGrpc(FlairQuery query, String connectionLinkId, String result) {
        log.info("Putting grpc result into a cache request for connection {} query {} result {}",
                connectionLinkId, query, result);

        String cacheKey;
        try {
            cacheKey = objectMapper.writeValueAsString(query);
        } catch (JsonProcessingException e) {
            log.error("Error creating a cache key for query " + query + " and connection " + connectionLinkId);
            return;
        }

        try {
            PutCacheResponse putCache = getCacheServiceStub().putCache(com.flair.bi.messages.PutCacheRequest.newBuilder()
                    .setKey(cacheKey)
                    .setTable(connectionLinkId)
                    .setValue(result)
                    .build());
        } catch(StatusRuntimeException e) {
            log.error("Error saving data to cache " + e.getMessage() + " status " + e.getStatus() + " trailers " + e.getTrailers(), e);
        } catch (Exception e) {
            log.error("Error saving data to cache " + e.getMessage() + "", e);
        }
    }

}
