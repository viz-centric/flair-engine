package com.fbi.engine.query;

import com.fbi.engine.config.FlairCachingConfig;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.domain.query.Query;
import com.fbi.engine.query.abstractfactory.QueryAbstractFactory;
import com.fbi.engine.query.factory.FlairFactory;
import com.fbi.engine.service.cache.CacheMetadata;
import com.fbi.engine.service.cache.CacheParams;
import com.fbi.engine.service.cache.FlairCachingService;
import com.project.bi.exceptions.CompilationException;
import com.project.bi.exceptions.ExecutionException;
import com.project.bi.query.FlairQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {

    private final QueryAbstractFactory queryAbstractFactory;
    private final FlairCachingService flairCachingService;
    private final FlairCachingConfig flairCachingConfig;

    @Override
    public CacheMetadata executeQuery(final Connection connection, final FlairQuery flairQuery) {
        return executeQuery(connection, flairQuery, new CacheParams());
    }

    @Override
    public CacheMetadata executeQuery(Connection connection, FlairQuery flairQuery, CacheParams cacheParams) {
        log.info("Executing flair query {} with cache params {} cache enabled {}",
                flairQuery.getStatement(), cacheParams, flairCachingConfig.isEnabled());

        boolean cachingEnabled = flairCachingConfig.isEnabled() && cacheParams.isReadFromCache();
        if (!cachingEnabled) {
            return queryAndPutToCache(connection, flairQuery, cacheParams);
        }

        Optional<CacheMetadata> result = getCachedResult(connection, flairQuery);

        log.info("Cached result found={} for query {}", result.isPresent(), flairQuery.getStatement());

        return result.orElseGet(() -> queryAndPutToCache(connection, flairQuery, cacheParams));
    }

    @Override
    public Query compileQuery(Connection connection, FlairQuery flairQuery) {
        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());
        return compileQuery(flairQuery, flairFactory);
    }

    private CacheMetadata queryAndPutToCache(Connection connection, FlairQuery flairQuery, CacheParams cacheParams) {
        boolean cachingEnabled = flairCachingConfig.isEnabled() && cacheParams.isWriteToCache();
        CacheMetadata queryResult = queryDatasource(connection, flairQuery);
        if (cachingEnabled) {
            flairCachingService.putResultAsync(flairQuery, connection.getLinkId(), queryResult.getResult(), cacheParams);
        }
        return queryResult;
    }

    private Optional<CacheMetadata> getCachedResult(Connection connection, FlairQuery flairQuery) {
        return flairCachingService.getResult(flairQuery, connection.getLinkId());
    }

    private CacheMetadata queryDatasource(Connection connection, FlairQuery flairQuery) {
        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());

        final Query query = compileQuery(flairQuery, flairFactory);

        log.info("Interpreted Query: {}", query.getQuery());

        StringWriter writer2 = new StringWriter();
        try {
            QueryExecutor executor = flairFactory.getExecutor(connection);
            executor.execute(query, writer2);
        } catch (ExecutionException e) {
            log.error("Error executing statement " + query.getQuery(), e);
            return new CacheMetadata();
        }

        CacheMetadata cacheMetadata = new CacheMetadata();
        cacheMetadata.setResult(writer2.toString());
        cacheMetadata.setStale(false);
        return cacheMetadata;
    }

    private Query compileQuery(FlairQuery flairQuery, FlairFactory flairFactory) {
        StringWriter writer = new StringWriter();

        try {
            flairFactory.getCompiler().compile(flairQuery, writer);
        } catch (CompilationException e) {
            throw new RuntimeException(e);
        }

        return flairFactory.getQuery(flairQuery, writer.toString());
    }

}
