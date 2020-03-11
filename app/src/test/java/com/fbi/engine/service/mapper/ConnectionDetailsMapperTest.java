package com.fbi.engine.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fbi.engine.domain.details.AthenaConnectionDetails;
import com.fbi.engine.domain.details.KafkaConnectionDetails;
import com.fbi.engine.domain.details.PostgresConnectionDetails;
import com.google.common.collect.ImmutableMap;

@ExtendWith(MockitoExtension.class)
public class ConnectionDetailsMapperTest {

	private ConnectionDetailsMapper mapper;

	@BeforeEach
	public void setUp() throws Exception {
		mapper = new ConnectionDetailsMapper();
	}

	@Test
	public void mapToEntityWorks() {
		PostgresConnectionDetails connectionDetails = (PostgresConnectionDetails) mapper.mapToEntity(ImmutableMap
				.of("@type", "Postgres", "databaseName", "services", "serverIp", "domain.com", "serverPort", "1414"));

		assertEquals(1414, (int) connectionDetails.getServerPort());
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

		assertEquals(1414, (int) connectionDetails.getServerPort());
		assertEquals("services", connectionDetails.getDatabaseName());
		assertEquals("domain.com", connectionDetails.getServerIp());
		assertEquals("s3://athena", connectionDetails.getS3OutputLocation());
		assertEquals("wg", connectionDetails.getWorkgroup());
	}

	@Test
	public void mapToKafkaWorks() {
		KafkaConnectionDetails connectionDetails = (KafkaConnectionDetails) mapper
				.mapToEntity(ImmutableMap.of("@type", "Kafka", "databaseName", "services", "serverIp", "domain.com",
						"serverPort", "1414", "isSecure", "true"));

		assertEquals(1414, (int) connectionDetails.getServerPort());
		assertEquals("services", connectionDetails.getDatabaseName());
		assertEquals("domain.com", connectionDetails.getServerIp());
		assertEquals(true, connectionDetails.getIsSecure());
	}

	@Test
	public void mapToEntityThrowsExceptionForUnknownType() {
		assertThrows(RuntimeException.class, () -> mapper.mapToEntity(ImmutableMap.of("@type", "Postgres1",
				"databaseName", "services", "serverIp", "domain.com", "serverPort", "1414")));
	}

	@Test
	public void mapToEntityThrowsExceptionIfTypeAbsent() {
		assertThrows(RuntimeException.class, () -> mapper.mapToEntity(
				ImmutableMap.of("databaseName", "services", "serverIp", "domain.com", "serverPort", "1414")));
	}

	@Test
	public void entityToMapWorks() {
		Map<String, String> map = mapper.entityToMap(new PostgresConnectionDetails("localhost", 1212, "datasource", "param1=test"));

		assertEquals("localhost", map.get("serverIp"));
		assertEquals("1212", map.get("serverPort"));
		assertEquals("datasource", map.get("databaseName"));
		assertEquals("param1=test", map.get("connectionParams"));
		assertEquals("Postgres", map.get("@type"));
	}

	@Test
	public void athenaToMapWorks() {
		Map<String, String> map = mapper
				.entityToMap(new AthenaConnectionDetails("localhost", 1212, "datasource", "s3://athena", "wg"));

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
