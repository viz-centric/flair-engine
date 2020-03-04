package com.fbi.engine.plugins.mysql;

import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.api.DataSourceConnection;
import com.fbi.engine.api.DataSourceDriver;
import com.fbi.engine.plugins.core.DataSourceDriverImpl;
import com.fbi.engine.plugins.core.sql.DriverLoadingStrategy;
import com.fbi.engine.plugins.core.sql.SqlQueryExecutor;

public class MySqlQueryExecutor extends SqlQueryExecutor {

	public MySqlQueryExecutor(DriverLoadingStrategy strategy, DataSourceConnection connection,
			ObjectMapper objectMapper, DataSourceDriver driver) {
		super(strategy, connection, objectMapper, driver);
	}

	@Override
	protected String getDriverClassName() {
		return "com.mysql.jdbc.Driver";
	}

	@Override
	protected DataSourceDriver getDefaultDriver() {
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("mysql-connector-java-8.0.16.jar")) {
			return DataSourceDriverImpl.of(is, "mysql-connector-java", "mysql", "8.0.16");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
