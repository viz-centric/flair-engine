package com.fbi.engine.domain.details;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class AthenaConnectionDetailsTest {

	@Test
	public void getConnectionString() {
		AthenaConnectionDetails details = new AthenaConnectionDetails("server.com", 1231, "dbname",
				"s3://aws-eu-west-1/", "wg");
		String connectionString = details.getConnectionString();

		assertEquals("jdbc:awsathena://server.com:1231", connectionString);
		assertEquals("s3://aws-eu-west-1/", details.getS3OutputLocation());
		assertEquals("wg", details.getWorkgroup());
		assertEquals("dbname", details.getDatabaseName());
	}
}
