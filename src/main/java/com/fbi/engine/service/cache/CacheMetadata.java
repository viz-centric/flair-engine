package com.fbi.engine.service.cache;

import lombok.Data;

import java.time.Instant;

@Data
public class CacheMetadata {
    private String result;
    private Instant dateCreated;
}
