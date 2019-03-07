package com.fbi.engine.query;

import com.fbi.engine.domain.Connection;
import com.fbi.engine.domain.query.Query;
import com.fbi.engine.query.abstractfactory.QueryAbstractFactory;
import com.fbi.engine.query.factory.FlairFactory;
import com.fbi.engine.service.cache.CacheMetadata;
import com.fbi.engine.service.cache.FlairCachingService;
import com.project.bi.exceptions.CompilationException;
import com.project.bi.exceptions.ExecutionException;
import com.project.bi.query.FlairCompiler;
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
    private final FlairCachingService cachingService;

    @Override
    public String executeQuery(final Connection connection, final FlairQuery flairQuery) {
        log.info("Executing query {}", flairQuery);

        if (!flairQuery.isCacheEnabled()) {
            return queryDatasource(connection, flairQuery);
        }

        Optional<CacheMetadata> result = getCachedResult(connection, flairQuery);

        return result
                .map(cache -> cache.getResult())
                .orElseGet(() -> {
                    String queryResult = queryDatasource(connection, flairQuery);
                    addToCache(connection, flairQuery, queryResult);
                    return queryResult;
                });
    }

    private void addToCache(Connection connection, FlairQuery flairQuery, String queryResult) {
        if (flairQuery.isCacheEnabled()) {
            cachingService.putResultGrpc(flairQuery, connection.getLinkId(), queryResult);
        }
    }

    private Optional<CacheMetadata> getCachedResult(Connection connection, FlairQuery flairQuery) {
        return cachingService.getResultGrpc(flairQuery, connection.getLinkId());
    }

    private String queryDatasource(Connection connection, FlairQuery flairQuery) {
        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());

        FlairCompiler compiler = flairFactory.getCompiler();

        StringWriter writer = new StringWriter();

        try {
            compiler.compile(flairQuery, writer);
        } catch (CompilationException e) {
            throw new RuntimeException(e);
        }

        final Query query = flairFactory.getQuery(flairQuery, writer.toString());

        log.debug("Interpreted Query: {}", query.getQuery());

        StringWriter writer2 = new StringWriter();
        try {
            QueryExecutor executor = flairFactory.getExecutor(connection);
            executor.execute(query, writer2);
        } catch (ExecutionException e) {
            log.error("Error executing statement " + query.getQuery(), e);
            return null;
        }

        return writer2.toString();
    }

}
