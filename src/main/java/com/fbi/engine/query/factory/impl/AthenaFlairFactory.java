package com.fbi.engine.query.factory.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryExecutor;
import com.fbi.engine.query.executor.AthenaQueryExecutor;
import com.fbi.engine.query.factory.FlairFactory;
import com.flair.bi.compiler.athena.AthenaFlairCompiler;
import com.project.bi.query.FlairCompiler;

public class AthenaFlairFactory implements FlairFactory {

    @Override
    public FlairCompiler getCompiler() {
        return new AthenaFlairCompiler();
    }

    @Override
    public QueryExecutor getExecutor(Connection connection) {
        ObjectMapper obj = createObjectMapper();
        return new AthenaQueryExecutor(connection, obj);
    }

}
