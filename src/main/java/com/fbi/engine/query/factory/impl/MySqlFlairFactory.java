package com.fbi.engine.query.factory.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        ObjectMapper obj = createObjectMapper();
        return new MySqlQueryExecutor(connection, obj);
    }
}
