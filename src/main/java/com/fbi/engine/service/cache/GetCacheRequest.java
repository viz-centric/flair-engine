package com.fbi.engine.service.cache;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class GetCacheRequest {
    final String key;
    final String table;
}
