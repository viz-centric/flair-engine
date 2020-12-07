package com.fbi.engine.query.factory.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryExecutor;
import com.fbi.engine.query.executor.BigquerySqlQueryExecutor;
import com.fbi.engine.query.factory.FlairFactory;
import com.flair.bi.compiler.bigquery.BigqueryFlairCompiler;
import com.project.bi.query.FlairCompiler;

/**
 * Postgres implementation of {@link FlairFactory}
 */
public class BigqueryFlairFactory implements FlairFactory {

    private final BigqueryFlairCompiler compiler;

    public BigqueryFlairFactory() {
        compiler = new BigqueryFlairCompiler();
    }

    @Override
    public FlairCompiler getCompiler() {
        return compiler;
    }

    @Override
    public QueryExecutor getExecutor(Connection connection) {
        ObjectMapper obj = createObjectMapper();
        return new BigquerySqlQueryExecutor(connection, obj);
    }

}
