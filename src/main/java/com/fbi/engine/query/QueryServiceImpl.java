package com.fbi.engine.query;

import com.fbi.engine.domain.Connection;
import com.fbi.engine.domain.query.Query;
import com.fbi.engine.query.abstractfactory.QueryAbstractFactory;
import com.fbi.engine.query.factory.FlairFactory;
import com.project.bi.exceptions.CompilationException;
import com.project.bi.exceptions.ExecutionException;
import com.project.bi.query.FlairCompiler;
import com.project.bi.query.FlairQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {

    private final QueryAbstractFactory queryAbstractFactory;

    @Override
    public String executeQuery(Connection connection, FlairQuery flairQuery) {
        FlairFactory flairFactory = queryAbstractFactory.getQueryFactory(connection.getConnectionType().getBundleClass());

        FlairCompiler compiler = flairFactory.getCompiler();

        QueryExecutor executor = flairFactory.getExecutor(connection);

        StringWriter writer = new StringWriter();

        try {
            compiler.compile(flairQuery, writer);
        } catch (CompilationException e) {
            throw new RuntimeException(e);
        }

        Query query = flairFactory.getQuery(flairQuery, writer.toString());

        log.debug("Interpreted Query: {}", query.getQuery());

        StringWriter writer2 = new StringWriter();
        try {
            executor.execute(query, writer2);
        } catch (ExecutionException e) {
            log.error("Error executing statement " + query.getQuery(), e);
            return null;
        }

        return writer2.toString();
    }



}
