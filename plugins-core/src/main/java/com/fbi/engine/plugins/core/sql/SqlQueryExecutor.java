package com.fbi.engine.plugins.core.sql;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.api.DataSourceConnection;
import com.fbi.engine.api.DataSourceDriver;
import com.fbi.engine.api.Query;
import com.fbi.engine.api.QueryExecutor;
import com.fbi.engine.plugins.core.ResultSetConverter;
import com.project.bi.exceptions.ExecutionException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class SqlQueryExecutor implements QueryExecutor {

	private static final Map<ConnectionDataKey, ConnectionDataValue> connections = new ConcurrentHashMap<>();

	private static final String USERNAME = "username";

	private static final String PASSWORD = "password";

	protected final DriverLoadingStrategy strategy;

	protected final DataSourceConnection connection;

	protected final ObjectMapper objectMapper;

	protected final DataSourceDriver driver;

	protected abstract String getDriverClassName();

	protected abstract DataSourceDriver getDefaultDriver();

	protected Driver initDriver() throws ExecutionException {
		try {
			final Driver d = strategy.loadDriver(getDriverClassName(), driver == null ? getDefaultDriver() : driver);
			log.info("Driver successfully registered: {}", getDriverClassName());
			return d;
		} catch (DriverLoadingException e) {
			log.error("An error occured trying to load driver for SQL Query executor for driver class name: {}",
					getDriverClassName());
			throw new ExecutionException(e);
		}
	}

	protected void closeDriver(Driver driver) throws ExecutionException {
		try {
			DriverManager.deregisterDriver(driver);
			log.info("Driver successfully de-registered: {}", getDriverClassName());
		} catch (SQLException | SecurityException e) {
			log.error(e.getLocalizedMessage(), e);
			throw new ExecutionException(e);
		}
	}

	protected Properties constructConnectionProperties() {
		Properties prop = new Properties();
		prop.put("user", this.connection.getConnectionProperties().get(USERNAME));
		prop.putAll(this.connection.getConnectionProperties());
		return prop;
	}

	@Override
	public void execute(Query query, Writer writer) throws ExecutionException {
		final Driver driver = initDriver();
		final String connectionString = this.connection.getConnectionString();
		final String connectionUsername = this.connection.getConnectionProperties().getProperty(USERNAME);
		final String connectionPassword = this.connection.getConnectionProperties().getProperty(PASSWORD);

		ConnectionDataValue connectionDataValue = getConnection(connectionString, connectionUsername,
				connectionPassword);

		try (final Connection c = connectionDataValue.getDataSource().getConnection()) {
			if (c != null) {
				try (final Statement statement = c.createStatement()) {
					statement.execute(query.getQuery());
					try (ResultSet resultSet = statement.getResultSet()) {
						writer.write(
								new ResultSetConverter(objectMapper, query.isMetadataRetrieved()).convert(resultSet));
					}
				}
				log.debug("Connection closed");
			} else {
				log.error("Failed to make connection! Connection string: {}", connection.getConnectionString());
				throw new ExecutionException("Failed to create a connection");
			}
		} catch (SQLTransientConnectionException e) {
			log.error("Connection to database timed out, stacktrace: {}", e.getMessage());
			throw new ExecutionException("Database timed out", e);
		} catch (SQLException e) {
			log.error("Connection to database failed, stacktrace: {}", e.getMessage());
			throw new ExecutionException("Database threw an exception", e);
		} catch (IOException e) {
			log.error("Reading data failed, message: {}", e.getMessage());
			throw new ExecutionException("Reading data failed", e);
		} finally {
			closeDriver(driver);
		}
	}

	private static ConnectionDataValue getConnection(String jdbcUrl, String username, String password) {
		return connections.computeIfAbsent(new ConnectionDataKey(jdbcUrl, username, password),
				connectionData -> createConnectionValue(connectionData));
	}

	private static ConnectionDataValue createConnectionValue(ConnectionDataKey connectionData) {
		return new ConnectionDataValue(createHikariConnection(connectionData));
	}

	private static DataSource createHikariConnection(ConnectionDataKey connectionData) {
		log.info("Creating new hikari data source for {}", connectionData.getJdbcUrl());
		HikariConfig config = new HikariConfig();
		config.setReadOnly(true);
		config.setConnectionTimeout(300_000);
		config.setMaximumPoolSize(50);
		config.setMinimumIdle(1);
		config.setIdleTimeout(60_000);
		config.setJdbcUrl(connectionData.getJdbcUrl());
		config.setUsername(connectionData.getUsername());
		config.setPassword(connectionData.getPassword());
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
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
