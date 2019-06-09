package com.fbi.engine.query.factory.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryExecutor;
import com.fbi.engine.query.executor.OracleSqlQueryExecutor;
import com.fbi.engine.query.factory.FlairFactory;
import com.flair.bi.compiler.oracle.OracleFlairCompiler;
import com.project.bi.query.FlairCompiler;

/**
 * Oracle implementation of {@link FlairCompiler}
 */
public class OracleFlairFactory implements FlairFactory {

    private final OracleFlairCompiler compiler;

    public OracleFlairFactory() {
        compiler = new OracleFlairCompiler();
    }

    @Override
    public FlairCompiler getCompiler() {
        return compiler;
    }

    @Override
    public QueryExecutor getExecutor(Connection connection) {
        ObjectMapper obj = createObjectMapper();
        return new OracleSqlQueryExecutor(connection, obj);
    }


}
