package com.fbi.engine.query.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.domain.details.ConnectionDetails;
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
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Class that is responsible for gettign connection to given SQL database and executing statement
 */
@Slf4j
@RequiredArgsConstructor
public abstract class SqlQueryExecutor implements QueryExecutor {

    static {
        // Put the redshift driver at the end so that it doesn't
        // conflict with postgres queries
        java.util.Enumeration<Driver> drivers =  DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver d = drivers.nextElement();
            if (d.getClass().getName().equals("com.amazon.redshift.jdbc.Driver")) {
                try {
                    DriverManager.deregisterDriver(d);
                    DriverManager.registerDriver(d);
                } catch (SQLException e) {
                    throw new RuntimeException("Could not deregister redshift driver");
                }
                break;
            }
        }
    }

    private static final Map<ConnectionDataKey, ConnectionDataValue> connections = new ConcurrentHashMap<>();
    protected final Connection connection;
    protected final ObjectMapper objectMapper;

    @Override
    public void execute(Query query, Writer writer) throws ExecutionException {
        invokeQuery(query, writer);
    }

    protected void invokeQuery(Query query, Writer writer) throws ExecutionException {
        String connectionUsername = this.connection.getConnectionUsername();
        String connectionPassword = this.connection.getConnectionPassword();
        ConnectionDetails details = this.connection.getDetails();

        ConnectionDataValue connectionDataValue = getConnection(
                details,
                connectionUsername,
                connectionPassword
        );

        java.sql.Connection c = null;

        try {
            if (connectionDataValue == null) {
                c = DriverManager.getConnection(
                        this.connection.getDetails().getConnectionString(),
                        this.connection.getConnectionUsername(),
                        this.connection.getConnectionPassword());
            } else {
                c = connectionDataValue.getDataSource().getConnection();
            }

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
        } finally {
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException t) {
                    throw new RuntimeException(t);
                }
            }
        }

    }

    private ConnectionDataValue getConnection(ConnectionDetails connectionDetails, String username, String password) {
        return connections.computeIfAbsent(
                new ConnectionDataKey(connectionDetails, username, password),
                connectionData -> createConnectionValue(connectionData)
        );
    }

    private ConnectionDataValue createConnectionValue(ConnectionDataKey connectionData) {
        if (connectionData.connectionDetails.isExternal()) {
            return null;
        }
        DataSource hikariConnection = createHikariConnection(connectionData, getConnectionProperties());
        return new ConnectionDataValue(hikariConnection);
    }

    protected Properties getConnectionProperties() {
        return new Properties();
    }

    private static DataSource createHikariConnection(ConnectionDataKey connectionData, Properties dataSourceProperties) {
        String connectionString = connectionData.getConnectionDetails().getConnectionString();

        log.info("Creating new hikari data source for {}", connectionString);

        HikariConfig config = new HikariConfig();
        config.setDataSourceProperties(dataSourceProperties);
        config.setReadOnly(true);
        config.setConnectionTimeout(30_000);
        config.setMaximumPoolSize(50);
        config.setMinimumIdle(1);
        config.setIdleTimeout(60_000);
        config.setJdbcUrl(connectionString);
        config.setUsername(connectionData.getUsername());
        config.setPassword(connectionData.getPassword());
        return new HikariDataSource(config);
    }

    @Data
    @RequiredArgsConstructor
    private static class ConnectionDataKey {
        private final ConnectionDetails connectionDetails;
        private final String username;
        private final String password;
    }

    @Data
    @RequiredArgsConstructor
    private static class ConnectionDataValue {
        private final DataSource dataSource;
    }
}
