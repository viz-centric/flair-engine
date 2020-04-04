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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@Slf4j
public class QueryServiceImpl implements QueryService {

    private final ExecutorService executorService;
    private final QueryAbstractFactory queryAbstractFactory;
    private final FlairCachingService flairCachingService;
    private final FlairCachingConfig flairCachingConfig;

    public QueryServiceImpl(QueryAbstractFactory queryAbstractFactory,
                            FlairCachingService flairCachingService,
                            FlairCachingConfig flairCachingConfig,
                            @Value("${query-management.max-threads:10}") Integer maxQueryThreads) {
        this.queryAbstractFactory = queryAbstractFactory;
        this.flairCachingService = flairCachingService;
        this.flairCachingConfig = flairCachingConfig;
        this.executorService = Executors.newFixedThreadPool(maxQueryThreads);
    }

    @Override
    public CacheMetadata executeQuery(final Connection connection, final FlairQuery flairQuery) {
        return executeQuery(connection, flairQuery, new CacheParams());
    }

    @Override
    public CacheMetadata executeQuery(Connection connection, FlairQuery flairQuery, CacheParams cacheParams) {
        log.info("Executing flair query {}", flairQuery.getStatement());

        boolean cachingEnabled = flairCachingConfig.isEnabled() && cacheParams.isReadFromCache();
        if (!cachingEnabled) {
            return queryAndPutToCache(connection, flairQuery, cacheParams);
        }

        Optional<CacheMetadata> result = getCachedResult(connection, flairQuery);

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

        String result;
        try {
            QueryExecutor executor = flairFactory.getExecutor(connection);
            log.debug("Pre-running a query {}", query.getQuery());
            Future<String> future = executorService.submit(() -> {
                log.debug("Executing a query {}", query.getQuery());
                StringWriter writer = new StringWriter();
                executor.execute(query, writer);
                return writer.toString();
            });
            result = future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interruption happened while invoking query " + e.getMessage(), e);
        } catch (java.util.concurrent.ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ExecutionException) {
                log.error("Error happened while invoking query " + query.getQuery(), cause);
                return new CacheMetadata();
            }
            throw new RuntimeException("Error happened while invoking query " + query.getQuery(), e);
        }

        CacheMetadata cacheMetadata = new CacheMetadata();
        cacheMetadata.setResult(result);
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
