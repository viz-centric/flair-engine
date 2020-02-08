package com.fbi.engine.domain.details;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PostgresConnectionDetailsTest {

    @Test
    public void getConnectionString() {
        PostgresConnectionDetails details = new PostgresConnectionDetails("localhost", 1234, "dbname", "param1=test");
        assertEquals("localhost", details.getServerIp());
        assertEquals("dbname", details.getDatabaseName());
        assertEquals("param1=test", details.getConnectionParams());
        assertEquals("jdbc:postgresql://localhost:1234/dbname?param1=test", details.getConnectionString());
        assertEquals(1234, (int) details.getServerPort());
    }
}
