package com.fbi.engine.plugins.mysql;

import java.util.Properties;

import org.junit.BeforeClass;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.api.DataSourceConnection;
import com.fbi.engine.api.DataSourceDriver;
import com.fbi.engine.plugins.core.DataSourceDriverImpl;
import com.fbi.engine.plugins.core.json.JacksonFactory;
import com.fbi.engine.plugins.core.sql.DriverLoadingStrategy;
import com.fbi.engine.plugins.core.sql.DynamicDriverLoadingStrategy;
import com.fbi.engine.plugins.test.AbstractQueryExecutorUnitTest;

public class MySqlQueryExecutorIntegrationTest extends AbstractQueryExecutorUnitTest<MySqlQueryExecutor> {

	private DataSourceDriver driver = DataSourceDriverImpl.of("mysql-connector-java-8.0.16.jar", "mysql-connector-java",
			"mysql", "8.0.16");

	private ObjectMapper objectMapper = JacksonFactory.getInstance().getObjectMapper();

	private DriverLoadingStrategy strategy = new DynamicDriverLoadingStrategy();

	private static int port = -1;

	@BeforeClass
	public static void retrievePort() {
		port = Integer.parseInt(System.getenv("it-database.port"));
	}

	@Override
	protected MySqlQueryExecutor configureQueryExecutor() {
		return new MySqlQueryExecutor(strategy, new DataSourceConnection() {

			@Override
			public String getConnectionString() {
				return "jdbc:mysql://localhost:" + port + "/services";
			}

			@Override
			public Properties getConnectionProperties() {
				Properties properties = new Properties();
				properties.put("username", "mysql");
				properties.put("password", "admin");
				return properties;
			}
		}, objectMapper, driver);
	}

	@Override
	protected MySqlQueryExecutor misconfigureQueryExecutor() {
		return new MySqlQueryExecutor(strategy, new DataSourceConnection() {

			@Override
			public String getConnectionString() {
				return "jdbc:mysql://localhost:" + port + "/notWOrking";
			}

			@Override
			public Properties getConnectionProperties() {
				Properties properties = new Properties();
				properties.put("username", "mysql");
				properties.put("password", "admin");
				return properties;
			}
		}, objectMapper, driver);
	}

}