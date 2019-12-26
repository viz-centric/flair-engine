package com.fbi.engine.plugins.postgres;

import java.io.File;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.util.Properties;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.MountableFile;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fbi.engine.api.DataSourceConnection;
import com.fbi.engine.api.DataSourceDriver;
import com.fbi.engine.api.Query;
import com.fbi.engine.plugins.core.DataSourceDriverImpl;
import com.fbi.engine.plugins.core.ResultSetSerializer;
import com.fbi.engine.plugins.core.sql.DriverLoadingStrategy;
import com.fbi.engine.plugins.core.sql.DynamicDriverLoadingStrategy;
import com.project.bi.exceptions.ExecutionException;

public class PostgresQueryExecutorTest {

	@ClassRule
	public static GenericContainer<?> CONTAINER = new GenericContainer<>("postgres:9.6.12")
			.withEnv("POSTGRES_USER", "postgres").withEnv("POSTGRES_PASSWORD", "admin")
			.withEnv("POSTGRES_DB", "services").withExposedPorts(5432).withCopyFileToContainer(
					MountableFile.forClasspathResource("init.sql"), "/docker-entrypoint-initdb.d/init.sql");

	private PostgresQueryExecutor sut;

	private DataSourceConnection connection;

	private DataSourceDriver driver;

	private ObjectMapper obj;

	private DriverLoadingStrategy strat = new DynamicDriverLoadingStrategy();

	@Before
	public void init() {
		driver = DataSourceDriverImpl.of(new File("src/test/resources/postgresql-9.4.1212.jar"), "postgresql",
				"org.postgresql", "9.4.1212");
		connection = new DataSourceConnection() {

			@Override
			public String getConnectionString() {
				return "jdbc:postgresql://localhost:" + CONTAINER.getFirstMappedPort() + "/services";
			}

			@Override
			public Properties getConnectionProperties() {
				Properties properties = new Properties();
				properties.put("username", "postgres");
				properties.put("password", "admin");
				return properties;
			}
		};

		obj = new ObjectMapper();
		SimpleModule module = new SimpleModule("MyModule", new Version(1, 0, 0, null, null, null));
		module.addSerializer(ResultSet.class, new ResultSetSerializer());
		obj.registerModule(module);
		sut = new PostgresQueryExecutor(strat, connection, obj, driver);

	}

	@Test
	public void testConnectionIsWorking() throws Exception {
		final StringWriter writer = new StringWriter();
		sut.execute(new Query() {

			@Override
			public boolean isMetadataRetrieved() {
				return false;
			}

			@Override
			public String getQuery() {
				return "select 1";
			}
		}, writer);
		writer.close();
	}

	@Test
	public void testQueryIsWorking() throws Exception {
		final StringWriter writer = new StringWriter();
		sut.execute(new Query() {

			@Override
			public boolean isMetadataRetrieved() {
				return false;
			}

			@Override
			public String getQuery() {
				return "select * from transactions";
			}
		}, writer);
		writer.close();
	}

	@Test(expected = ExecutionException.class)
	public void testQueryNotWorking() throws Exception {
		final StringWriter writer = new StringWriter();
		sut.execute(new Query() {

			@Override
			public boolean isMetadataRetrieved() {
				return false;
			}

			@Override
			public String getQuery() {
				return "select * from notexists";
			}
		}, writer);
		writer.close();
	}

	@Test(expected = ExecutionException.class)
	public void testConnectionNotWorking() throws Exception {
		sut = new PostgresQueryExecutor(strat, new DataSourceConnection() {

			@Override
			public String getConnectionString() {
				return "jdbc:postgresql://localhost:" + CONTAINER.getFirstMappedPort() + "/notExistingDatabase";
			}

			@Override
			public Properties getConnectionProperties() {
				Properties properties = new Properties();
				properties.put("username", "postgres");
				properties.put("password", "admin");
				return properties;
			}
		}, obj, driver);

		final StringWriter writer = new StringWriter();
		sut.execute(new Query() {

			@Override
			public boolean isMetadataRetrieved() {
				return false;
			}

			@Override
			public String getQuery() {
				return "select 1";
			}
		}, writer);
		writer.close();

	}

}
