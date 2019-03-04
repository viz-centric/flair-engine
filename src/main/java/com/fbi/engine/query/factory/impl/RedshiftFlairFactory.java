package com.fbi.engine.query.factory.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fbi.engine.config.jackson.ResultSetSerializer;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryExecutor;
import com.fbi.engine.query.executor.RedshiftQueryExecutor;
import com.fbi.engine.query.factory.FlairFactory;
import com.flair.bi.compiler.redshift.RedshiftFlairCompiler;
import com.project.bi.query.FlairCompiler;

public class RedshiftFlairFactory implements FlairFactory {

    @Override
    public FlairCompiler getCompiler() {
        return new RedshiftFlairCompiler();
    }

    @Override
    public QueryExecutor getExecutor(Connection connection) {
        ObjectMapper obj = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(new ResultSetSerializer());
        obj.registerModule(module);
        return new RedshiftQueryExecutor(connection, obj);
    }

}
