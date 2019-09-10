package com.fbi.engine.query.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.Connection;

public class OracleSqlQueryExecutor extends SqlQueryExecutor {

    public OracleSqlQueryExecutor(Connection connection, ObjectMapper objectMapper) {
        super(connection, objectMapper);
    }

    /**
     * Loads the JDBC driver required for Oracle
     */
    @Override
    protected void loadDrivers() throws ClassNotFoundException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
    }

}
