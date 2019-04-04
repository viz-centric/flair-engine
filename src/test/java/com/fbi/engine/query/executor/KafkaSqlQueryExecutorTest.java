package com.fbi.engine.query.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.FbiengineApp;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.domain.details.KafkaConnectionDetails;
import com.fbi.engine.domain.query.KafkaQuery;
import com.fbi.engine.query.dto.KafkaKsqlDescribeResponse;
import com.fbi.engine.query.dto.KafkaShowTablesResponse;
import com.fbi.engine.query.factory.impl.KafkaFlairFactory;
import com.flair.bi.compiler.kafka.KafkaListener;
import com.project.bi.query.FlairQuery;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.StringWriter;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FbiengineApp.class)
@Transactional
public class KafkaSqlQueryExecutorTest {

    private KafkaSqlQueryExecutor executor;

    @Mock
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        Connection connection = new Connection();
        KafkaConnectionDetails details = new KafkaConnectionDetails();
        details.setServerPort(1234);
        details.setServerIp("localhost");
        connection.setDetails(details);
        executor = new KafkaSqlQueryExecutor(connection, objectMapper, restTemplate, new KafkaFlairFactory());
    }

    @Test(expected = RuntimeException.class)
    public void loadDrivers() {
        executor.loadDrivers();
    }

    @Ignore
    @Test
    public void executeSelectQuery() throws Exception {
        String statement = "select height, width, position, x, y from measurements limit 10";

        StringWriter writer = new StringWriter();

        when(restTemplate.exchange(eq("http://localhost:1234/query"), eq(HttpMethod.POST), any(HttpEntity.class),
                any(Class.class))).thenReturn(ResponseEntity.ok(
                        "{\"row\":{\"columns\":[1524760769983,\"1\",1524760769747,\"alice\",\"home\"]},\"errorMessage\":null}"
                                + "\n"
                                + "{\"row\":{\"columns\":[1524760769984,\"2\",1524760769748,\"alice2\",\"home2\"]},\"errorMessage\":null}"));

        FlairQuery flairQuery = new FlairQuery(statement, false);
        executor.execute(new KafkaQuery(statement, false, "dbname", flairQuery), writer);

        assertEquals("{\n" + "  \"data\" : [ {\n" + "    \"width\" : \"1\",\n" + "    \"x\" : \"alice\",\n"
                + "    \"y\" : \"home\",\n" + "    \"position\" : 1524760769747,\n" + "    \"height\" : 1524760769983\n"
                + "  }, {\n" + "    \"width\" : \"2\",\n" + "    \"x\" : \"alice2\",\n" + "    \"y\" : \"home2\",\n"
                + "    \"position\" : 1524760769748,\n" + "    \"height\" : 1524760769984\n" + "  } ]\n" + "}",
                writer.toString());
    }

    @Ignore
    @Test
    public void executeSelectQueryWithMetadata() throws Exception {
        String statement = "select height, width, position, x, y from measurements limit 10";

        StringWriter writer = new StringWriter();

        when(restTemplate.exchange(eq("http://localhost:1234/query"), eq(HttpMethod.POST), any(HttpEntity.class),
                any(Class.class))).thenReturn(ResponseEntity.ok(
                        "{\"row\":{\"columns\":[1524760769983,\"1\",1524760769747,\"alice\",\"home\"]},\"errorMessage\":null}"));

        when(restTemplate.exchange(eq("http://localhost:1234/ksql"), eq(HttpMethod.POST), any(HttpEntity.class),
                any(Class.class))).thenReturn(ResponseEntity
                        .ok(new KafkaKsqlDescribeResponse[] { new KafkaKsqlDescribeResponse().setSourceDescription(
                                new KafkaKsqlDescribeResponse.SourceDescription().setFields(Arrays.asList(
                                        new KafkaKsqlDescribeResponse.Field().setName("first")
                                                .setSchema(new KafkaKsqlDescribeResponse.Schema().setType("firstType")),
                                        new KafkaKsqlDescribeResponse.Field().setName("second").setSchema(
                                                new KafkaKsqlDescribeResponse.Schema().setType("secondType")),
                                        new KafkaKsqlDescribeResponse.Field().setName("third")
                                                .setSchema(new KafkaKsqlDescribeResponse.Schema().setType("thirdType")),
                                        new KafkaKsqlDescribeResponse.Field().setName("fourth")
                                                .setSchema(new KafkaKsqlDescribeResponse.Schema().setType("thirdType")),
                                        new KafkaKsqlDescribeResponse.Field().setName("fifth").setSchema(
                                                new KafkaKsqlDescribeResponse.Schema().setType("thirdType"))))) }));

        FlairQuery flairQuery = new FlairQuery(statement, true);
        KafkaQuery kafkaQuery = new KafkaQuery(statement, true, "dbname", flairQuery);
        executor.execute(kafkaQuery, writer);

        assertEquals("{\n" + "  \"metadata\" : {\n" + "    \"third\" : \"thirdType\",\n"
                + "    \"fifth\" : \"thirdType\",\n" + "    \"fourth\" : \"thirdType\",\n"
                + "    \"first\" : \"firstType\",\n" + "    \"second\" : \"secondType\"\n" + "  },\n"
                + "  \"data\" : [ {\n" + "    \"third\" : 1524760769983,\n" + "    \"fifth\" : \"1\",\n"
                + "    \"fourth\" : 1524760769747,\n" + "    \"first\" : \"alice\",\n" + "    \"second\" : \"home\"\n"
                + "  } ]\n" + "}", writer.toString());

        assertEquals("select third,fifth,fourth,first,second from dbname limit 1", kafkaQuery.getQuery());
    }

    @Ignore
    @Test
    public void executeSelectShowTablesQuery() throws Exception {
        String statement = KafkaListener.QUERY__SHOW_TABLES_AND_STREAMS;

        StringWriter writer = new StringWriter();

        when(restTemplate.exchange(eq("http://localhost:1234/ksql"), eq(HttpMethod.POST), any(HttpEntity.class),
                any(Class.class)))
                        .thenReturn(ResponseEntity.ok(new KafkaShowTablesResponse[] { new KafkaShowTablesResponse()
                                .setTables(Arrays.asList(new KafkaShowTablesResponse.Table().setName("table1"),
                                        new KafkaShowTablesResponse.Table().setName("table2")))
                                .setStreams(Arrays.asList(new KafkaShowTablesResponse.Table().setName("table1"),
                                        new KafkaShowTablesResponse.Table().setName("stream2"))) }));

        FlairQuery flairQuery = new FlairQuery(statement, false);
        executor.execute(new KafkaQuery(statement, false, "dbname", flairQuery), writer);

        assertEquals("{\n" + "  \"data\" : [ {\n" + "    \"tablename\" : \"table2\"\n" + "  }, {\n"
                + "    \"tablename\" : \"table1\"\n" + "  }, {\n" + "    \"tablename\" : \"stream2\"\n" + "  } ]\n"
                + "}", writer.toString());
    }

}
