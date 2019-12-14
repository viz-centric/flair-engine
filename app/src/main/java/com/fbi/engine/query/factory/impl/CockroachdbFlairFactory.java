package com.fbi.engine.query.factory.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryExecutor;
import com.fbi.engine.query.executor.CockroachdbQueryExecutor;
import com.fbi.engine.query.factory.FlairFactory;
import com.flair.bi.compiler.cockroachdb.CockroachdbFlairCompiler;
import com.project.bi.query.FlairCompiler;

public class CockroachdbFlairFactory implements FlairFactory {

    @Override
    public FlairCompiler getCompiler() {
        return new CockroachdbFlairCompiler();
    }

    @Override
    public QueryExecutor getExecutor(Connection connection) {
        ObjectMapper obj = createObjectMapper();
        return new CockroachdbQueryExecutor(connection, obj);
    }

}
