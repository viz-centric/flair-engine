package com.fbi.engine.service.cache;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Accessors(chain = true)
public class CacheMetadata {
    private String result;
    private Instant dateCreated;
    private boolean isStale;
}
