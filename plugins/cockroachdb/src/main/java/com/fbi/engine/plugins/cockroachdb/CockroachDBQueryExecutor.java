package com.fbi.engine.plugins.cockroachdb;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.api.DataSourceConnection;
import com.fbi.engine.api.DataSourceDriver;
import com.fbi.engine.plugins.core.DataSourceDriverImpl;
import com.fbi.engine.plugins.core.sql.DriverLoadingStrategy;
import com.fbi.engine.plugins.core.sql.SqlQueryExecutor;

public class CockroachDBQueryExecutor extends SqlQueryExecutor {

	public CockroachDBQueryExecutor(DriverLoadingStrategy strategy, DataSourceConnection connection,
			ObjectMapper objectMapper, DataSourceDriver driver) {
		super(strategy, connection, objectMapper, driver);
	}

	@Override
	protected String getDriverClassName() {
		return "org.postgresql.Driver";
	}

	@Override
	protected DataSourceDriver getDefaultDriver() {
		return DataSourceDriverImpl.of(new File("src/main/resources/postgresql-9.4.1212.jar"), "postgresql",
				"org.postgresql", "9.4.1212");
	}

}