package com.fbi.engine.query.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.domain.query.Query;
import com.fbi.engine.query.QueryExecutor;
import com.fbi.engine.query.convert.impl.ResultSetConverter;
import com.project.bi.exceptions.ExecutionException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Class that is responsible for gettign connection to given SQL database and executing statement
 */
@Slf4j
@RequiredArgsConstructor
public abstract class SqlQueryExecutor implements QueryExecutor {

    private static final Map<ConnectionDataKey, ConnectionDataValue> connections = new ConcurrentHashMap<>();
    protected final Connection connection;
    protected final ObjectMapper objectMapper;

    @Override
    public void execute(Query query, Writer writer) throws ExecutionException {
        String connectionString = this.connection.getDetails().getConnectionString();
        String connectionUsername = this.connection.getConnectionUsername();
        String connectionPassword = this.connection.getConnectionPassword();

        ConnectionDataValue connectionDataValue = getConnection(connectionString, connectionUsername, connectionPassword);

        try (java.sql.Connection c = connectionDataValue.getDataSource().getConnection()) {
            log.debug("Connection obtained, executing query {}", query.getQuery());
            try (Statement statement = c.createStatement()) {
                statement.execute(query.getQuery());
                try (ResultSet resultSet = statement.getResultSet()) {
                    writer.write(new ResultSetConverter(objectMapper, query.isMetadataRetrieved()).convert(resultSet));
                }
            }
        } catch (SQLTransientConnectionException e) {
            log.error("Connection to database timed out, stacktrace: {}", e.getMessage());
            throw new ExecutionException("Database timed out", e);
        } catch (SQLException e) {
            log.error("Connection to database failed, stacktrace: {}", e.getMessage());
            throw new ExecutionException("Database threw an exception", e);
        } catch (IOException e) {
            log.error("Deserialization of data failed, message: {}", e.getMessage());
            throw new ExecutionException("Reading data failed", e);
        }
    }

    private static ConnectionDataValue getConnection(String jdbcUrl, String username, String password) {
        return connections.computeIfAbsent(
                new ConnectionDataKey(jdbcUrl, username, password),
                connectionData -> createConnectionValue(connectionData)
        );
    }

    private static ConnectionDataValue createConnectionValue(ConnectionDataKey connectionData) {
        return new ConnectionDataValue(createHikariConnection(connectionData));
    }

    private static DataSource createHikariConnection(ConnectionDataKey connectionData) {
        log.info("Creating new hikari data source for {}", connectionData.getJdbcUrl());
        HikariConfig config = new HikariConfig();
        config.setReadOnly(true);
        config.setConnectionTimeout(30_000);
        config.setMaximumPoolSize(50);
        config.setMinimumIdle(1);
        config.setIdleTimeout(60_000);
        config.setJdbcUrl(connectionData.getJdbcUrl());
        config.setUsername(connectionData.getUsername());
        config.setPassword(connectionData.getPassword());
        return new HikariDataSource(config);
    }

    @Data
    @RequiredArgsConstructor
    private static class ConnectionDataKey {
        private final String jdbcUrl;
        private final String username;
        private final String password;
    }

    @Data
    @RequiredArgsConstructor
    private static class ConnectionDataValue {
        private final DataSource dataSource;
    }
}
