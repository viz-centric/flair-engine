package com.fbi.engine.plugins.postgres;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fbi.engine.api.Connection;
import com.fbi.engine.api.DataSourceDriver;
import com.fbi.engine.api.Query;
import com.fbi.engine.plugins.core.ResultSetSerializer;
import com.fbi.engine.plugins.core.sql.DynamicDriverLoadingStrategy;

public class PostgresQueryExecutorTest {

	private PostgresQueryExecutor sut;

	private Connection connection = new Connection() {

		@Override
		public String getConnectionString() {
			return "jdbc:postgresql://localhost:5429/services?ssl=false";
		}

		@Override
		public Map<String, Object> getConnectionProperties() {
			final Map<String, Object> map = new HashMap<>();
			map.put("username", "postgres");
			map.put("password", "admin");
			return map;
		}
	};

	private DataSourceDriver driver = new DataSourceDriver() {

		@Override
		public String getVersion() {
			return "9.4.1212";
		}

		@Override
		public byte[] getJar() {
			try {
				final File file = new File("src/test/resources/postgresql-9.4.1212.jar");
				byte[] fileContent = Files.readAllBytes(file.toPath());
				return fileContent;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}

		@Override
		public String getGroupId() {
			return "org.postgresql";
		}

		@Override
		public String getArtifactId() {
			return "postgresql";
		}
	};

	@Before
	public void init() {
		ObjectMapper obj = new ObjectMapper();
		SimpleModule module = new SimpleModule("MyModule", new Version(1, 0, 0, null, null, null));
		module.addSerializer(ResultSet.class, new ResultSetSerializer());
		obj.registerModule(module);
		sut = new PostgresQueryExecutor(new DynamicDriverLoadingStrategy(), connection, obj, driver);
	}

	@Test
	public void testQuery() throws Exception {
		File file = new File("src/test/resources/test.json");
		FileWriter writer = new FileWriter(file);
		sut.execute(new Query() {

			@Override
			public boolean isMetadataRetrieved() {
				return false;
			}

			@Override
			public String getQuery() {
				return "select * from ecommerce where order_item_id = 37312;";
			}
		}, writer);
		writer.close();
	}

}
