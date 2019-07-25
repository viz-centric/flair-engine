package com.fbi.engine.service.mapper;

import com.fbi.engine.domain.details.AthenaConnectionDetails;
import com.fbi.engine.domain.details.KafkaConnectionDetails;
import com.fbi.engine.domain.details.PostgresConnectionDetails;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionDetailsMapperTest {

    private ConnectionDetailsMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new ConnectionDetailsMapper();
    }

    @Test
    public void mapToEntityWorks() {
        PostgresConnectionDetails connectionDetails = (PostgresConnectionDetails) mapper.mapToEntity(ImmutableMap.of(
            "@type", "Postgres",
            "databaseName", "services",
            "serverIp", "domain.com",
            "serverPort", "1414"
        ));

        assertEquals(1414, (int)connectionDetails.getServerPort());
        assertEquals("services", connectionDetails.getDatabaseName());
        assertEquals("domain.com", connectionDetails.getServerIp());
    }

    @Test
    public void mapToAthenaWorks() {
        Map<String, String> map = new HashMap<>();
        map.put("@type", "Athena");
        map.put("databaseName", "services");
        map.put("serverIp", "domain.com");
        map.put("serverPort", "1414");
        map.put("s3OutputLocation", "s3://athena");
        map.put("workgroup", "wg");
        AthenaConnectionDetails connectionDetails = (AthenaConnectionDetails) mapper.mapToEntity(map);

        assertEquals(1414, (int)connectionDetails.getServerPort());
        assertEquals("services", connectionDetails.getDatabaseName());
        assertEquals("domain.com", connectionDetails.getServerIp());
        assertEquals("s3://athena", connectionDetails.getS3OutputLocation());
        assertEquals("wg", connectionDetails.getWorkgroup());
    }

    @Test
    public void mapToKafkaWorks() {
        KafkaConnectionDetails connectionDetails = (KafkaConnectionDetails) mapper.mapToEntity(ImmutableMap.of(
            "@type", "Kafka",
            "databaseName", "services",
            "serverIp", "domain.com",
            "serverPort", "1414",
            "isSecure", "true"
        ));

        assertEquals(1414, (int)connectionDetails.getServerPort());
        assertEquals("services", connectionDetails.getDatabaseName());
        assertEquals("domain.com", connectionDetails.getServerIp());
        assertEquals(true, connectionDetails.getIsSecure());
    }

    @Test(expected = RuntimeException.class)
    public void mapToEntityThrowsExceptionForUnknownType() {
        mapper.mapToEntity(ImmutableMap.of(
            "@type", "Postgres1",
            "databaseName", "services",
            "serverIp", "domain.com",
            "serverPort", "1414"
        ));
    }

    @Test(expected = RuntimeException.class)
    public void mapToEntityThrowsExceptionIfTypeAbsent() {
        mapper.mapToEntity(ImmutableMap.of(
            "databaseName", "services",
            "serverIp", "domain.com",
            "serverPort", "1414"
        ));
    }

    @Test
    public void entityToMapWorks() {
        Map<String, String> map = mapper.entityToMap(new PostgresConnectionDetails("localhost", 1212, "datasource"));

        assertEquals("localhost", map.get("serverIp"));
        assertEquals("1212", map.get("serverPort"));
        assertEquals("datasource", map.get("databaseName"));
        assertEquals("Postgres", map.get("@type"));
    }

    @Test
    public void athenaToMapWorks() {
        Map<String, String> map = mapper.entityToMap(new AthenaConnectionDetails("localhost", 1212, "datasource", "s3://athena", "wg"));

        assertEquals("localhost", map.get("serverIp"));
        assertEquals("1212", map.get("serverPort"));
        assertEquals("datasource", map.get("databaseName"));
        assertEquals("Athena", map.get("@type"));
        assertEquals("s3://athena", map.get("s3OutputLocation"));
        assertEquals("wg", map.get("workgroup"));
    }

    @Test
    public void kafkaToMapWorks() {
        Map<String, String> map = mapper.entityToMap(new KafkaConnectionDetails("localhost", 1212, "datasource", true));

        assertEquals("localhost", map.get("serverIp"));
        assertEquals("1212", map.get("serverPort"));
        assertEquals("datasource", map.get("databaseName"));
        assertEquals("Kafka", map.get("@type"));
        assertEquals("true", map.get("isSecure"));
    }

}
