package com.fbi.engine.query;

import com.fbi.engine.config.FlairCachingConfig;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.domain.details.BigqueryConnectionDetails;
import com.fbi.engine.domain.details.ConnectionDetails;
import com.fbi.engine.domain.query.Query;
import com.fbi.engine.query.abstractfactory.QueryAbstractFactory;
import com.fbi.engine.query.factory.FlairFactory;
import com.fbi.engine.service.auditlog.QueryAuditLogService;
import com.fbi.engine.service.auditlog.QueryLogParams;
import com.fbi.engine.service.cache.CacheMetadata;
import com.fbi.engine.service.cache.CacheParams;
import com.fbi.engine.service.cache.FlairCachingService;
import com.fbi.engine.service.cache.QueryParams;
import com.project.bi.exceptions.CompilationException;
import com.project.bi.exceptions.ExecutionException;
import com.project.bi.query.FlairQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {

    private final QueryAbstractFactory queryAbstractFactory;
    private final FlairCachingService flairCachingService;
    private final FlairCachingConfig flairCachingConfig;
    private final QueryAuditLogService queryAuditLogService;
    private final QueryResultPostProcessor queryResultPostProcessor;

    @Override
    public CacheMetadata executeQuery(QueryParams queryParams) {
        FlairQuery flairQuery = queryParams.getFlairQuery();
        CacheParams cacheParams = queryParams.getCacheParams();

        log.info("Executing flair query {} with cache params {} cache enabled {}",
                flairQuery.getStatement(), cacheParams, flairCachingConfig.isEnabled());

        boolean cachingEnabled = flairCachingConfig.isEnabled() && cacheParams.isReadFromCache();
        if (!cachingEnabled) {
            return queryAndPutToCache(queryParams);
        }

        Optional<CacheMetadata> result = getCachedResult(queryParams);

        log.info("Cached result found={} for query {}", result.isPresent(), flairQuery.getStatement());

        return result.orElseGet(() -> queryAndPutToCache(queryParams));
    }

    @Override
    public Query compileQuery(Connection connection, FlairQuery flairQuery) {
        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());
        return compileQuery(flairQuery, flairFactory);
    }

    private CacheMetadata queryAndPutToCache(QueryParams queryParams) {
        CacheParams cacheParams = queryParams.getCacheParams();
        Connection connection = queryParams.getConnection();
        boolean cachingEnabled = flairCachingConfig.isEnabled() && cacheParams.isWriteToCache();
        CacheMetadata queryResult = queryDatasource(queryParams);
        if (cachingEnabled) {
            flairCachingService.putResultAsync(queryResult.getInterpretedQuery(), connection.getLinkId(), queryResult.getResult(), cacheParams);
        }
        return queryResult;
    }

    private Optional<CacheMetadata> getCachedResult(QueryParams queryParams) {
        FlairQuery flairQuery = queryParams.getFlairQuery();
        Connection connection = queryParams.getConnection();
        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());
        Query query = compileQuery(flairQuery, flairFactory);

        queryAuditLogService.recordQuery(QueryLogParams.builder()
                .connection(connection)
                .query(query.getQuery())
                .username(queryParams.getUsername())
                .meta(queryParams.getMetadata())
                .build());

        return flairCachingService.getResult(query.getQuery(), connection.getLinkId());
    }

    private CacheMetadata queryDatasource(QueryParams queryParams) {
        Connection connection = queryParams.getConnection();
        FlairQuery flairQuery = queryParams.getFlairQuery();

        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());
        Query query = compileQuery(flairQuery, flairFactory);

        log.info("Interpreted Query: {}", query.getQuery());
        queryAuditLogService.recordQuery(QueryLogParams.builder()
                .connection(connection)
                .query(query.getQuery())
                .username(queryParams.getUsername())
                .meta(queryParams.getMetadata())
                .build());

        String linkId = connection.getLinkId();
        Path path = null;
        if (StringUtils.isEmpty(linkId)) {
            ConnectionDetails details = connection.getDetails();
            if (details instanceof BigqueryConnectionDetails) {
                String privateKey = ((BigqueryConnectionDetails) details).getPrivateKey();
                try {
                    path = Files.createTempFile(Math.random() + "", ".json");
                    Files.write(path, privateKey.getBytes(StandardCharsets.UTF_8));
                    ((BigqueryConnectionDetails) details).setPrivateKeyPath(path.toAbsolutePath().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        StringWriter writer2 = new StringWriter();
        try {
            QueryExecutor executor = flairFactory.getExecutor(connection);
            executor.execute(query, writer2);
        } catch (ExecutionException e) {
            log.error("Error executing statement " + query.getQuery(), e);
            return new CacheMetadata();
        } finally {
            if (path != null) {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        String result = writer2.toString();
        String newResult = queryResultPostProcessor.process(flairQuery, result);

        CacheMetadata cacheMetadata = new CacheMetadata();
        cacheMetadata.setInterpretedQuery(query.getQuery());
        cacheMetadata.setResult(newResult);
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
