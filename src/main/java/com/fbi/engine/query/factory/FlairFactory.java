package com.fbi.engine.query.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fbi.engine.config.jackson.ResultSetSerializer;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.domain.query.GenericQuery;
import com.fbi.engine.domain.query.Query;
import com.fbi.engine.query.QueryExecutor;
import com.project.bi.general.Factory;
import com.project.bi.query.FlairCompiler;
import com.project.bi.query.FlairQuery;

import java.text.SimpleDateFormat;

/**
 * Factory that instantiates all necessary components that are needed to communicate with external data sources.
 */
public interface FlairFactory extends Factory {


    /**
     * Instantiate a compiler for given data source
     *
     * @return instance of {@link FlairCompiler}
     */
    FlairCompiler getCompiler();

    /**
     * Instantiate a executor for a given data source
     *
     * @param connection connection to given data source
     * @return instance of {@link QueryExecutor}
     */
    QueryExecutor getExecutor(Connection connection);

    @Override
    default String getFactoryName() {
        return getClass().getName();
    }

    default Query getQuery(FlairQuery flairQuery, String statement) {
        return new GenericQuery(statement, flairQuery.isPullMeta());
    }

    default ObjectMapper createObjectMapper() {
        ObjectMapper obj = new ObjectMapper();
        obj.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        obj.setDateFormat(new SimpleDateFormat(FlairFactoryConst.DATASOURCE_TIMESTAMP_FORMAT));
        SimpleModule module = new SimpleModule();
        module.addSerializer(new ResultSetSerializer());
        obj.registerModule(module);
        return obj;
    }
}
