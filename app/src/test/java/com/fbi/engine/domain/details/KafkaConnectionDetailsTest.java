package com.fbi.engine.domain.details;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class KafkaConnectionDetailsTest {

	@Test
	public void getConnectionStringSecure() {
		KafkaConnectionDetails details = new KafkaConnectionDetails();
		details.setIsSecure(true);
		details.setDatabaseName("dbname");
		details.setServerIp("localhost");
		details.setServerPort(1414);

		String connectionString = details.getConnectionString();

		assertEquals("https://localhost:1414", connectionString);
	}

	@Test
	public void getConnectionStringInsecure() {
		KafkaConnectionDetails details = new KafkaConnectionDetails();
		details.setIsSecure(false);
		details.setDatabaseName("dbname");
		details.setServerIp("localhost");
		details.setServerPort(1414);

		String connectionString = details.getConnectionString();

		assertEquals("http://localhost:1414", connectionString);
	}
}
