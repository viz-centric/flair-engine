package com.fbi.engine.query.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.Connection;

public class PostgresSqlQueryExecutor extends SqlQueryExecutor {

    public PostgresSqlQueryExecutor(Connection connection, ObjectMapper objectMapper) {
        super(connection, objectMapper);
    }

    @Override
    protected void loadDrivers() throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
    }

}
