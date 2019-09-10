package com.fbi.engine.query.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.Connection;

/**
 * Executes the SQL query returned by the Query method and returns results
 */
public class MySqlQueryExecutor extends SqlQueryExecutor {

    public MySqlQueryExecutor(Connection connection, ObjectMapper objectMapper) {
        super(connection, objectMapper);
    }

    /**
     * Loads the JDBC driver required for MySQL
     */
    @Override
    protected void loadDrivers() throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
    }


}
