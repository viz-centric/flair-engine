package com.fbi.engine.plugins.core.sql;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.api.DataSourceConnection;
import com.fbi.engine.api.DataSourceDriver;
import com.fbi.engine.api.Query;
import com.fbi.engine.api.QueryExecutor;
import com.fbi.engine.plugins.core.ResultSetConverter;
import com.project.bi.exceptions.ExecutionException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class SqlQueryExecutor implements QueryExecutor {

	private static final String USERNAME = "username";

	protected final DriverLoadingStrategy strategy;

	protected final DataSourceConnection connection;

	protected final ObjectMapper objectMapper;

	protected final DataSourceDriver driver;

	protected abstract String getDriverClassName();

	protected abstract DataSourceDriver getDefaultDriver();

	protected Driver initDriver() throws ExecutionException {
		try {
			final Driver d = strategy.loadDriver(getDriverClassName(), driver);
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
		try (final Connection c = DriverManager.getConnection(this.connection.getConnectionString(),
				constructConnectionProperties())) {
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

}
