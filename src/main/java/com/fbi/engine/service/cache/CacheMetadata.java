package com.fbi.engine.service.cache;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.Optional;

@Data
@Accessors(chain = true)
public class CacheMetadata {
    private String result;
    private Instant dateCreated;
    private boolean isStale;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CacheMetadata{");
        sb.append("result='").append(Optional.ofNullable(result).map(r -> "***").orElse(null)).append('\'');
        sb.append(", dateCreated=").append(dateCreated);
        sb.append(", isStale=").append(isStale);
        sb.append('}');
        return sb.toString();
    }
}
