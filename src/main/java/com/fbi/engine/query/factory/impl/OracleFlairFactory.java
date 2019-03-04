package com.fbi.engine.query.factory.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fbi.engine.config.jackson.ResultSetSerializer;
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
        ObjectMapper obj = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(new ResultSetSerializer());
        obj.registerModule(module);
        return new OracleSqlQueryExecutor(connection, obj);
    }


}
