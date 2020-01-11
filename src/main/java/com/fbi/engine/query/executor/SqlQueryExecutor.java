package com.fbi.engine.query.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.domain.query.Query;
import com.fbi.engine.query.QueryExecutor;
import com.fbi.engine.query.convert.impl.ResultSetConverter;
import com.project.bi.exceptions.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Writer;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Class that is responsible for gettign connection to given SQL database and executing statement
 */
@Slf4j
@RequiredArgsConstructor
public abstract class SqlQueryExecutor implements QueryExecutor {

    protected final Connection connection;

    protected final ObjectMapper objectMapper;


    /**
     * Load drivers for specific database management system
     *
     * @throws ClassNotFoundException if driver is not found
     */
    protected abstract void loadDrivers() throws ClassNotFoundException;

    @Override
    public void execute(Query query, Writer writer) throws ExecutionException {
        try {
            loadDrivers();
        } catch (ClassNotFoundException e) {
            log.error("Driver is not supported: {}", e.getMessage());
            throw new ExecutionException("Driver not supported");
        }

        try (java.sql.Connection c = DriverManager.getConnection(
            this.connection.getDetails().getConnectionString(),
            this.connection.getConnectionUsername(),
            this.connection.getConnectionPassword())) {
            if (c != null) {
                try (Statement statement = c.createStatement()) {
                    statement.execute(query.getQuery());
                    try (ResultSet resultSet = statement.getResultSet()) {
                        writer.write(new ResultSetConverter(objectMapper, query.isMetadataRetrieved()).convert(resultSet));
                    }
                }
                log.debug("Connection closed");
            } else {
                log.error("Failed to make connection!");
                throw new ExecutionException("Failed to create a connection");
            }

        } catch (SQLException e) {
            log.error("Connection to database failed, stacktrace: {}", e.getMessage());
            throw new ExecutionException("Database threw an exception", e);
        } catch (IOException e) {
            log.error("Reading data failed, message: {}", e.getMessage());
            throw new ExecutionException("Reading data failed", e);
        }
    }
}
