package com.fbi.engine.plugins.spark;

import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.api.DataSourceConnection;
import com.fbi.engine.api.DataSourceDriver;
import com.fbi.engine.plugins.core.DataSourceDriverImpl;
import com.fbi.engine.plugins.core.sql.DriverLoadingStrategy;
import com.fbi.engine.plugins.core.sql.SqlQueryExecutor;

public class SparkQueryExecutor extends SqlQueryExecutor {

	public SparkQueryExecutor(DriverLoadingStrategy strategy, DataSourceConnection connection,
			ObjectMapper objectMapper, DataSourceDriver driver) {
		super(strategy, connection, objectMapper, driver);
	}

	@Override
	protected String getDriverClassName() {
		return "org.apache.hive.jdbc.HiveDriver";
	}

	@Override
	protected DataSourceDriver getDefaultDriver() {
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("hive-jdbc-1.2.1.jar")) {
			return DataSourceDriverImpl.of(is, "hive-jdbc", "org.apache.hive", "1.2.1");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}