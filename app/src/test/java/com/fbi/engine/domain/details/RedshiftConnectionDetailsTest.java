package com.fbi.engine.domain.details;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RedshiftConnectionDetailsTest {

	@Test
	public void getConnectionString() {
		RedshiftConnectionDetails details = new RedshiftConnectionDetails("server.com", 1231, "dbname");
		String connectionString = details.getConnectionString();

		assertEquals("jdbc:redshift://server.com:1231/dbname", connectionString);
	}
}
