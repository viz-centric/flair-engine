package com.fbi.engine.plugins.mysql;

import java.util.Properties;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.MountableFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.api.DataSourceConnection;
import com.fbi.engine.api.DataSourceDriver;
import com.fbi.engine.plugins.core.DataSourceDriverImpl;
import com.fbi.engine.plugins.core.json.JacksonFactory;
import com.fbi.engine.plugins.core.sql.DriverLoadingStrategy;
import com.fbi.engine.plugins.core.sql.DynamicDriverLoadingStrategy;
import com.fbi.engine.plugins.test.AbstractQueryExecutorUnitTest;

public class MySqlQueryExecutorUnitTest extends AbstractQueryExecutorUnitTest<MySqlQueryExecutor> {

	private DataSourceDriver driver = DataSourceDriverImpl.of("mysql-connector-java-8.0.16.jar", "mysql-connector-java",
			"mysql", "8.0.16");

	private ObjectMapper objectMapper = JacksonFactory.getInstance().getObjectMapper();

	private DriverLoadingStrategy strategy = new DynamicDriverLoadingStrategy();

	@Override
	protected MySqlQueryExecutor configureQueryExecutor() {
		return new MySqlQueryExecutor(strategy, new DataSourceConnection() {

			@Override
			public String getConnectionString() {
				return "jdbc:mysql://localhost:" + container.getFirstMappedPort() + "/services";
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
				return "jdbc:mysql://localhost:" + container.getFirstMappedPort() + "/notWOrking";
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
	protected GenericContainer<?> configureTargetDataSource() {
		return new GenericContainer<>("mysql:8.0.16").withEnv("MYSQL_USER", "mysql").withEnv("MYSQL_PASSWORD", "admin")
				.withEnv("MYSQL_ROOT_PASSWORD", "root").withEnv("MYSQL_DATABASE", "services").withExposedPorts(3306)
				.withCopyFileToContainer(MountableFile.forClasspathResource("init.sql"),
						"/docker-entrypoint-initdb.d/init.sql");
	}

}
