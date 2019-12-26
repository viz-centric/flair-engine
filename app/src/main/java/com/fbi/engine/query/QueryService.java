package com.fbi.engine.query;

import com.fbi.engine.domain.Connection;
import com.fbi.engine.service.cache.CacheMetadata;
import com.fbi.engine.service.cache.CacheParams;
import com.project.bi.query.FlairQuery;

/**
 * Service responsible for communicating with other external data sources
 */
public interface QueryService {

	CacheMetadata executeQuery(Connection sources, FlairQuery query);

	CacheMetadata executeQuery(Connection sources, FlairQuery query, CacheParams cacheParams);

}
