package com.fbi.engine.query.executor;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.domain.query.Query;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jongo.Jongo;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Executes the SQL query returned by the Query method and returns results
 */
@Slf4j
public class MongoDBQueryExecutor extends SqlQueryExecutor {

    public MongoDBQueryExecutor(Connection connection, ObjectMapper objectMapper) {
        super(connection, objectMapper);
    }

    @Override
    public void execute(Query query, Writer writer) {

        DB db;
        try (MongoClient mongo = new MongoClient(connection.getDetails().getServerIp(), connection.getDetails().getServerPort())) {

            db = mongo.getDB(connection.getDetails().getDatabaseName());

            Jongo jongo = new Jongo(db);

            String q = query.getQuery();

            String jquery = q.substring(q.indexOf("["), q.lastIndexOf("]") + 1);

            org.jongo.MongoCollection collection = jongo.getCollection(q.substring(0, q.indexOf(".")));


            Object obj1 = JSON.parse(jquery);
            Object res = new Object();

            ObjectMapper mapper = new ObjectMapper();

            if (obj1 instanceof List) {

                List<Object> queryList = (List<Object>) obj1;

                try {
                    if (queryList.size() == 2) {
                        res = collection.aggregate(JSON.serialize(queryList.get(0))).and(JSON.serialize(queryList.get(1))).as(Object.class);
                    } else if (queryList.size() == 1) {
                        res = collection.aggregate(JSON.serialize(queryList.get(0))).as(Object.class);
                    }
                    String result = mapper.writeValueAsString(res);
                    writer.write(result);
                } catch (IOException e) {
                    log.error("Error running collection query", e);
                }
            }

        }

    }


}
