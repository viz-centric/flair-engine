package com.fbi.engine.plugins.mongodb;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.sql.Driver;
import java.util.List;

import org.jongo.Jongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.api.DataSourceConnection;
import com.fbi.engine.api.DataSourceDriver;
import com.fbi.engine.api.Query;
import com.fbi.engine.plugins.core.DataSourceDriverImpl;
import com.fbi.engine.plugins.core.sql.DriverLoadingStrategy;
import com.fbi.engine.plugins.core.sql.SqlQueryExecutor;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.util.JSON;
import com.project.bi.exceptions.ExecutionException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MongoDBQueryExecutor extends SqlQueryExecutor {

	public MongoDBQueryExecutor(DriverLoadingStrategy strategy, DataSourceConnection connection,
			ObjectMapper objectMapper, DataSourceDriver driver) {
		super(strategy, connection, objectMapper, driver);
	}

	@Override
	protected String getDriverClassName() {
		return "mongodb.jdbc.MongoDriver";
	}

	@Override
	protected DataSourceDriver getDefaultDriver() {
		return DataSourceDriverImpl.of(new File("src/main/resources/mongo-java-driver-3.7.1.jar"), "mongo-java-driver",
				"org.mongodb", "3.7.1");
	}

	@Override
	public void execute(Query query, Writer writer) throws ExecutionException {
		final Driver driver = initDriver();
		final MongoClientURI connectionString = new MongoClientURI(connection.getConnectionString());

		try (MongoClient client = new MongoClient(connectionString)) {

			final DB db = client.getDB(connection.getConnectionProperties().getProperty("databaseName"));

			Jongo jongo = new Jongo(db);

			String q = query.getQuery();

			String jquery = q.substring(q.indexOf("["), q.lastIndexOf("]") + 1);

			org.jongo.MongoCollection collection = jongo.getCollection(q.substring(0, q.indexOf(".")));

			Object obj1 = JSON.parse(jquery);
			Object res = new Object();

			ObjectMapper mapper = new ObjectMapper();

			if (obj1 instanceof List) {

				final List<Object> queryList = (List<Object>) obj1;

				try {
					if (queryList.size() == 2) {
						res = collection.aggregate(JSON.serialize(queryList.get(0)))
								.and(JSON.serialize(queryList.get(1))).as(Object.class);
					} else if (queryList.size() == 1) {
						res = collection.aggregate(JSON.serialize(queryList.get(0))).as(Object.class);
					}
					String result = mapper.writeValueAsString(res);
					writer.write(result);
				} catch (IOException e) {
					log.error("Error running collection query", e);
				}
			}

		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			throw new ExecutionException(e);
		} finally {
			closeDriver(driver);
		}

	}

}
