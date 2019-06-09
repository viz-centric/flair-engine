package com.fbi.engine.query.factory.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryExecutor;
import com.fbi.engine.query.executor.MongoDBQueryExecutor;
import com.fbi.engine.query.factory.FlairFactory;
import com.flair.bi.compiler.mongodb.MongoDBFlairCompiler;
import com.project.bi.query.FlairCompiler;

/**
 * Spark implementation of {@link FlairFactory}
 */
public class MongoDBFlairFactory implements FlairFactory {

    private final MongoDBFlairCompiler compiler;

    public MongoDBFlairFactory() {
        compiler = new MongoDBFlairCompiler();
    }

    @Override
    public FlairCompiler getCompiler() {
        return compiler;
    }

    @Override
    public QueryExecutor getExecutor(Connection connection) {
        ObjectMapper obj = createObjectMapper();
        return new MongoDBQueryExecutor(connection, obj);
    }


}
