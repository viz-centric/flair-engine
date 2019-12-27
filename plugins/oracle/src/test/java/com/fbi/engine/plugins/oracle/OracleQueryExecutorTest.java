package com.fbi.engine.plugins.oracle;

import java.io.File;
import java.util.Properties;

import org.junit.Ignore;
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

@Ignore
public class OracleQueryExecutorTest extends AbstractQueryExecutorUnitTest<OracleQueryExecutor> {

	private DataSourceDriver driver = DataSourceDriverImpl.of(new File("src/main/resources/ojdbc6-11.2.0.3.jar"),
			"ojdbc6", "oracle", "11.2.0.3");

	private ObjectMapper obj = JacksonFactory.getInstance().getObjectMapper();

	private DriverLoadingStrategy strat = new DynamicDriverLoadingStrategy();

	@Override
	protected OracleQueryExecutor configureQueryExecutor() {
		return new OracleQueryExecutor(strat, new DataSourceConnection() {

			@Override
			public String getConnectionString() {
				return "jdbc:oracle:thin:@//localhost:" + container.getFirstMappedPort() + "/xe";
//				return "jdbc:oracle:thin:@//localhost:1521/xe";
			}

			@Override
			public Properties getConnectionProperties() {
				Properties properties = new Properties();
				properties.put("username", "sys as sysdba");
				properties.put("password", "oracle");
				return properties;
			}
		}, obj, driver);
	}

	@Override
	protected String testConnection() {
		return "SELECT * FROM dual";
	}

	@Override
	protected OracleQueryExecutor misconfigureQueryExecutor() {
		return new OracleQueryExecutor(strat, new DataSourceConnection() {

			@Override
			public String getConnectionString() {
				return "jdbc:oracle:thin:@//localhost:" + container.getFirstMappedPort() + "/notExist";
			}

			@Override
			public Properties getConnectionProperties() {
				Properties properties = new Properties();
				properties.put("username", "sys as sysdba");
				properties.put("password", "oracle");
				return properties;
			}
		}, obj, driver);
	}

	@SuppressWarnings("resource")
	@Override
	protected GenericContainer<?> configureTargetDataSource() {
		return new GenericContainer<>("christophesurmont/oracle-xe-11g").withEnv("ORACLE_DISABLE_ASYNCH_IO", "true")
				.withEnv("ORACLE_ALLOW_REMOTE", "true").withLogConsumer(x -> {
					System.out.println(x.getUtf8String());
				}).withExposedPorts(1521).withCopyFileToContainer(MountableFile.forClasspathResource("init.sql"),
						"/docker-entrypoint-initdb.d/init.sql");
	}

}
