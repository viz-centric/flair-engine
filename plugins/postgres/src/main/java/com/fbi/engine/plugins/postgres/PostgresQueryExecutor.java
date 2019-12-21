package com.fbi.engine.plugins.postgres;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.api.Connection;
import com.fbi.engine.api.DataSourceDriver;
import com.fbi.engine.plugins.core.sql.DriverLoadingStrategy;
import com.fbi.engine.plugins.core.sql.SqlQueryExecutor;

public class PostgresQueryExecutor extends SqlQueryExecutor {

	public PostgresQueryExecutor(DriverLoadingStrategy strategy, Connection connection, ObjectMapper objectMapper,
			DataSourceDriver driver) {
		super(strategy, connection, objectMapper, driver);
	}

	@Override
	protected String getDriverClassName() {
		return "org.postgresql.Driver";
	}

}
