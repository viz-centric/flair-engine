package com.fbi.engine.query.factory.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryExecutor;
import com.fbi.engine.query.executor.SnowflakeSqlQueryExecutor;
import com.fbi.engine.query.factory.FlairFactory;
import com.flair.bi.compiler.snowflake.SnowflakeFlairCompiler;
import com.project.bi.query.FlairCompiler;

/**
 * Postgres implementation of {@link FlairFactory}
 */
public class SnowflakeFlairFactory implements FlairFactory {

    private final SnowflakeFlairCompiler compiler;

    public SnowflakeFlairFactory() {
        compiler = new SnowflakeFlairCompiler();
    }

    @Override
    public FlairCompiler getCompiler() {
        return compiler;
    }

    @Override
    public QueryExecutor getExecutor(Connection connection) {
        ObjectMapper obj = createObjectMapper();
        return new SnowflakeSqlQueryExecutor(connection, obj);
    }

}
