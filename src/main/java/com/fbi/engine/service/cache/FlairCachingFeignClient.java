package com.fbi.engine.service.cache;

import com.fbi.engine.caching.FeignClientConfig;
import feign.Headers;
import feign.RequestLine;
import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient(name = "flair-cache", configuration = FeignClientConfig.class)
@Headers({"Content-Type: application/json", "Accept: application/json"})
public interface FlairCachingFeignClient {

    @RequestLine("POST /cache/result")
    CacheResultResponse getCache(GetCacheRequest request);

    @RequestLine("PUT /cache/result")
    void putCache(PutCacheRequest request);

}
