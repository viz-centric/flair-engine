package com.fbi.engine.plugins.snowflake;

import java.io.File;

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
		return DataSourceDriverImpl.of(new File("src/main/resources/snowflake-jdbc-3.11.0.jar"), "snowflake-jdbc",
				"net.snowflake", "3.11.0");
	}

}