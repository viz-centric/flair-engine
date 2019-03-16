package com.fbi.engine.service.cache;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CacheParams {

    /**
     * Write value to cache if fetched from the database. Note that if "readFromCache" is set to
     * "true", then if cached value exists, then nothing will be written to the database.
     */
    private boolean writeToCache;

    /**
     * Read a value from cache and return it without going to the database
     */
    private boolean readFromCache;
}
