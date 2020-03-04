package com.fbi.engine.plugins.redshift;

import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.api.DataSourceConnection;
import com.fbi.engine.api.DataSourceDriver;
import com.fbi.engine.plugins.core.DataSourceDriverImpl;
import com.fbi.engine.plugins.core.sql.DriverLoadingStrategy;
import com.fbi.engine.plugins.core.sql.SqlQueryExecutor;

public class RedshiftQueryExecutor extends SqlQueryExecutor {

	public RedshiftQueryExecutor(DriverLoadingStrategy strategy, DataSourceConnection connection,
			ObjectMapper objectMapper, DataSourceDriver driver) {
		super(strategy, connection, objectMapper, driver);
	}

	@Override
	protected String getDriverClassName() {
		return "com.amazon.redshift.jdbc42.Driver";
	}

	@Override
	protected DataSourceDriver getDefaultDriver() {
		try (InputStream is = this.getClass().getClassLoader()
				.getResourceAsStream("redshift-jdbc42-no-awssdk-1.2.32.1056.jar")) {
			return DataSourceDriverImpl.of(is, "redshift-jdbc42-no-awssdk", "com.amazon.redshift", "1.2.32.1056");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}