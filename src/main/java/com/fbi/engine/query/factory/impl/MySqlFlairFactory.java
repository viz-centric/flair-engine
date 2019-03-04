package com.fbi.engine.query.factory.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fbi.engine.config.jackson.ResultSetSerializer;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryExecutor;
import com.fbi.engine.query.executor.MySqlQueryExecutor;
import com.fbi.engine.query.factory.FlairFactory;
import com.flair.bi.compiler.mysql.MySQLFlairCompiler;
import com.project.bi.query.FlairCompiler;

/**
 * Mysql implementation of {@link FlairFactory}
 */
public class MySqlFlairFactory implements FlairFactory {

    private final MySQLFlairCompiler compiler;

    public MySqlFlairFactory() {
        compiler = new MySQLFlairCompiler();
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
        return new MySqlQueryExecutor(connection, obj);
    }
}
