package com.fbi.engine.plugins.oracle;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.api.DataSourceConnection;
import com.fbi.engine.api.DataSourceDriver;
import com.fbi.engine.plugins.core.DataSourceDriverImpl;
import com.fbi.engine.plugins.core.sql.DriverLoadingStrategy;
import com.fbi.engine.plugins.core.sql.SqlQueryExecutor;

public class OracleQueryExecutor extends SqlQueryExecutor {

	public OracleQueryExecutor(DriverLoadingStrategy strategy, DataSourceConnection connection,
			ObjectMapper objectMapper, DataSourceDriver driver) {
		super(strategy, connection, objectMapper, driver);
	}

	@Override
	protected String getDriverClassName() {
		return "oracle.jdbc.driver.OracleDriver";
	}

	@Override
	protected DataSourceDriver getDefaultDriver() {
		return DataSourceDriverImpl.of(new File("src/main/resources/ojdbc6-11.2.0.3.jar"), "ojdbc6", "oracle",
				"11.2.0.3");
	}

}