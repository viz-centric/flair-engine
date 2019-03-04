package com.fbi.engine.domain.details;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class RedshiftConnectionDetailsTest {

    @Test
    public void getConnectionString() {
        RedshiftConnectionDetails details = new RedshiftConnectionDetails("server.com", 1231, "dbname");
        String connectionString = details.getConnectionString();

        assertEquals("jdbc:redshift://server.com:1231/dbname", connectionString);
    }
}
