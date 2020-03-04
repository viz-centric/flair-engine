package com.fbi.engine.plugins.snowflake;

import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.api.DataSourceConnection;
import com.fbi.engine.api.DataSourceDriver;
import com.fbi.engine.plugins.core.DataSourceDriverImpl;
import com.fbi.engine.plugins.core.sql.DriverLoadingStrategy;
import com.fbi.engine.plugins.core.sql.SqlQueryExecutor;

public class SnowflakeQueryExecutor extends SqlQueryExecutor {

	public SnowflakeQueryExecutor(DriverLoadingStrategy strategy, DataSourceConnection connection,
			ObjectMapper objectMapper, DataSourceDriver driver) {
		super(strategy, connection, objectMapper, driver);
	}

	@Override
	protected String getDriverClassName() {
		return "com.snowflake.client.jdbc.SnowflakeDriver";
	}

	@Override
	protected DataSourceDriver getDefaultDriver() {
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("snowflake-jdbc-3.11.0.jar")) {
			return DataSourceDriverImpl.of(is, "snowflake-jdbc", "net.snowflake", "3.11.0");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}