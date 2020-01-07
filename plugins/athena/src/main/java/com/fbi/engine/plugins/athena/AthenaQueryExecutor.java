package com.fbi.engine.plugins.athena;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.api.DataSourceConnection;
import com.fbi.engine.api.DataSourceDriver;
import com.fbi.engine.plugins.core.DataSourceDriverImpl;
import com.fbi.engine.plugins.core.sql.DriverLoadingStrategy;
import com.fbi.engine.plugins.core.sql.SqlQueryExecutor;

public class AthenaQueryExecutor extends SqlQueryExecutor {

	public AthenaQueryExecutor(DriverLoadingStrategy strategy, DataSourceConnection connection,
			ObjectMapper objectMapper, DataSourceDriver driver) {
		super(strategy, connection, objectMapper, driver);
	}

	@Override
	protected String getDriverClassName() {
		return "com.simba.athena.jdbc.Driver";
	}

	@Override
	protected DataSourceDriver getDefaultDriver() {
		return DataSourceDriverImpl.of(new File("src/main/resources/AthenaJDBC42-2.0.7.jar"), "AthenaJDBC42",
				"com.amazonaws.athena.jdbc", "2.0.7");
	}

}