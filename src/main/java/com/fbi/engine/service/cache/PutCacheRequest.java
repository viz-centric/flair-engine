package com.fbi.engine.service.cache;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PutCacheRequest {
    final String key;
    final String table;
    final String value;
}
