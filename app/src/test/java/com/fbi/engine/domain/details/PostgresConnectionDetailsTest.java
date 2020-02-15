package com.fbi.engine.domain.details;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PostgresConnectionDetailsTest {

	@Test
	public void getConnectionString() {
		PostgresConnectionDetails details = new PostgresConnectionDetails("localhost", 1234, "dbname");
		assertEquals("localhost", details.getServerIp());
		assertEquals("dbname", details.getDatabaseName());
		assertEquals("jdbc:postgresql://localhost:1234/dbname", details.getConnectionString());
		assertEquals(1234, (int) details.getServerPort());
	}
}
