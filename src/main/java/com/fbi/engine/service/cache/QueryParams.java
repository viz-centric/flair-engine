package com.fbi.engine.service.cache;

import com.fbi.engine.domain.Connection;
import com.fbi.engine.service.auditlog.QueryLogMeta;
import com.project.bi.query.FlairQuery;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryParams {

    private final Connection connection;
    private final FlairQuery flairQuery;
    private final CacheParams cacheParams;
    private final String username;
    private final QueryLogMeta metadata;

}
