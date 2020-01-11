package com.fbi.engine.query.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.Connection;

public class RedshiftQueryExecutor extends SqlQueryExecutor {

    public RedshiftQueryExecutor(Connection connection, ObjectMapper objectMapper) {
        super(connection, objectMapper);
    }

    @Override
    protected void loadDrivers() throws ClassNotFoundException {
        Class.forName("com.amazon.redshift.jdbc4.Driver");
    }

}
