package com.fbi.engine.query;

import com.fbi.engine.domain.Connection;
import com.fbi.engine.domain.query.Query;
import com.fbi.engine.service.cache.CacheMetadata;
import com.fbi.engine.service.cache.QueryParams;
import com.project.bi.query.FlairQuery;

/**
 * Service responsible for communicating with other external data sources
 */
public interface QueryService {

    CacheMetadata executeQuery(QueryParams queryParams);

    Query compileQuery(Connection sources, FlairQuery query);

}
