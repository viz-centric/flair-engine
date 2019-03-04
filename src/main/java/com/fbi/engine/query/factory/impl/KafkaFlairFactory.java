package com.fbi.engine.query.factory.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.domain.query.KafkaQuery;
import com.fbi.engine.domain.query.Query;
import com.fbi.engine.query.QueryExecutor;
import com.fbi.engine.query.executor.KafkaSqlQueryExecutor;
import com.fbi.engine.query.factory.FlairFactory;
import com.flair.bi.compiler.kafka.KafkaFlairCompiler;
import com.project.bi.query.FlairCompiler;
import com.project.bi.query.FlairQuery;
import org.springframework.web.client.RestTemplate;

public class KafkaFlairFactory implements FlairFactory {

    private final KafkaFlairCompiler compiler;

    public KafkaFlairFactory() {
        compiler = new KafkaFlairCompiler();
    }

    @Override
    public FlairCompiler getCompiler() {
        return compiler;
    }

    @Override
    public QueryExecutor getExecutor(Connection connection) {
        return new KafkaSqlQueryExecutor(connection, new ObjectMapper(), new RestTemplate(), this);
    }

    @Override
    public Query getQuery(FlairQuery flairQuery, String statement) {
        return new KafkaQuery(statement, flairQuery.isPullMeta(), flairQuery.getSource(), flairQuery);
    }
}
