package com.fbi.engine.plugins.postgres;

import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.api.DataSourceConnection;
import com.fbi.engine.api.DataSourceDriver;
import com.fbi.engine.plugins.core.DataSourceDriverImpl;
import com.fbi.engine.plugins.core.json.JacksonFactory;
import com.fbi.engine.plugins.core.sql.DriverLoadingStrategy;
import com.fbi.engine.plugins.core.sql.DynamicDriverLoadingStrategy;
import com.fbi.engine.plugins.test.AbstractQueryExecutorUnitTest;

public class PostgresQueryExecutorIntegrationTest extends AbstractQueryExecutorUnitTest<PostgresQueryExecutor> {

	private DataSourceDriver driver = DataSourceDriverImpl.of("postgresql-9.4.1212.jar", "postgresql", "org.postgresql",
			"9.4.1212");

	private ObjectMapper obj = JacksonFactory.getInstance().getObjectMapper();

	private DriverLoadingStrategy strat = new DynamicDriverLoadingStrategy();

	private static int port = 5432;
	private static String host = "it-postgres-database";

//	@BeforeClass
//	public static void retrievePort() {
//		port = Integer.parseInt(System.getProperty("it-database.port"));
//		host = System.getProperty("it-database.host");
//	}

	@Override
	protected PostgresQueryExecutor configureQueryExecutor() {
		return new PostgresQueryExecutor(strat, new DataSourceConnection() {

			@Override
			public String getConnectionString() {
				return "jdbc:postgresql://" + host + ":" + port + "/services";
			}

			@Override
			public Properties getConnectionProperties() {
				Properties properties = new Properties();
				properties.put("username", "postgres");
				properties.put("password", "admin");
				return properties;
			}
		}, obj, driver);
	}

	@Override
	protected PostgresQueryExecutor misconfigureQueryExecutor() {
		return new PostgresQueryExecutor(strat, new DataSourceConnection() {

			@Override
			public String getConnectionString() {
				return "jdbc:postgresql://" + host + ":" + port + "/notExistingDatabase";
			}

			@Override
			public Properties getConnectionProperties() {
				Properties properties = new Properties();
				properties.put("username", "postgres");
				properties.put("password", "admin");
				return properties;
			}
		}, obj, driver);
	}

}
