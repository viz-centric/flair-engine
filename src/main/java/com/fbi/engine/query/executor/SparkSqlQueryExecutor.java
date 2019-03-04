package com.fbi.engine.query.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fbi.engine.domain.Connection;
import com.fbi.engine.domain.query.Query;
import com.project.bi.exceptions.ExecutionException;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SparkSqlQueryExecutor extends SqlQueryExecutor {

    public SparkSqlQueryExecutor(Connection connection, ObjectMapper objectMapper) {
        super(connection, objectMapper);
    }

    /**
     * Loads the JDBC driver required for Oracle
     */
    @Override
    protected void loadDrivers() throws ClassNotFoundException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
    }

    @Override
    public void execute(Query query, Writer writer) throws ExecutionException {
        String res;
        Map<String, String> params = new HashMap<>();
        params.put("username", connection.getConnectionUsername());
        params.put("password", connection.getConnectionPassword());

        RestTemplate template = new RestTemplate();
        template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        LoginResponseSpark response = template.postForObject(connection.getDetails().getConnectionString(), params, LoginResponseSpark.class);

        MultiValueMap<String, String> m = new LinkedMultiValueMap<>();
        m.add("Accept", MediaType.APPLICATION_JSON.toString());
        m.add("Content-Type", MediaType.APPLICATION_JSON.toString());
        m.add("authorization", "JWT " + response.getToken());

        String myQuery = query.getQuery();

        HttpEntity<?> httpEntity = new HttpEntity<Object>(myQuery, m);

        //String url = "http://localhost:8000/api/v1/query/";
        StringBuilder connectionString = new StringBuilder();
        connectionString.append("http:");
        connectionString.append("//").append(connection.getDetails().getServerIp());
        connectionString.append(":").append(connection.getDetails().getServerPort());
        connectionString.append("/");
        connectionString.append(connection.getDetails().getDatabaseName());
        String url = connectionString.toString();

        List<String> list = template.postForObject(url, httpEntity, List.class);

        StringWriter stringWriter = new StringWriter();
        ObjectMapper o = new ObjectMapper();
        ObjectNode objectNode = o.createObjectNode();
        objectNode.putPOJO("data", list);
        try {
            o.writeValue(stringWriter, objectNode);
        } catch (IOException e) {
            throw new ExecutionException(e);
        }

        res = stringWriter.toString();

        res = res.replace("\\", "").toLowerCase();

        try {
            writer.write(res);
        } catch (IOException e) {
            throw new ExecutionException(e);
        }
    }
}
