package com.fbi.engine.query.factory.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fbi.engine.config.jackson.ResultSetSerializer;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryExecutor;
import com.fbi.engine.query.executor.PostgresSqlQueryExecutor;
import com.fbi.engine.query.factory.FlairFactory;
import com.flair.bi.compiler.postgres.PostgresFlairCompiler;
import com.project.bi.query.FlairCompiler;

/**
 * Postgres implementation of {@link FlairFactory}
 */
public class PostgresFlairFactory implements FlairFactory {

    private final PostgresFlairCompiler compiler;

    public PostgresFlairFactory() {
        compiler = new PostgresFlairCompiler();
    }

    @Override
    public FlairCompiler getCompiler() {
        return compiler;
    }

    @Override
    public QueryExecutor getExecutor(Connection connection) {
        ObjectMapper obj = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(new ResultSetSerializer());
        obj.registerModule(module);
        return new PostgresSqlQueryExecutor(connection, obj);
    }

}
