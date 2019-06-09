package com.fbi.engine.query.factory.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryExecutor;
import com.fbi.engine.query.executor.SparkSqlQueryExecutor;
import com.fbi.engine.query.factory.FlairFactory;
import com.flair.bi.compiler.spark.SparkFlairCompiler;
import com.project.bi.query.FlairCompiler;

/**
 * Spark implementation of {@link FlairFactory}
 */
public class SparkFlairFactory implements FlairFactory {

    private final SparkFlairCompiler compiler;

    public SparkFlairFactory() {
        compiler = new SparkFlairCompiler();
    }

    @Override
    public FlairCompiler getCompiler() {
        return compiler;
    }

    @Override
    public QueryExecutor getExecutor(Connection connection) {
        ObjectMapper obj = createObjectMapper();
        return new SparkSqlQueryExecutor(connection, obj);
    }


}
