package com.fbi.engine.service;

import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryService;
import com.fbi.engine.service.dto.ConnectionDTO;
import com.fbi.engine.service.mapper.ConnectionMapper;
import com.project.bi.query.FlairQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestConnectionServiceTest {

    private TestConnectionService service;

    @Mock
    private QueryService queryService;
    @Mock
    private ConnectionMapper connectionMapper;
    @Mock
    private ConnectionService connectionService;

    @Before
    public void setUp() throws Exception {
        service = new TestConnectionService(queryService, connectionService);
    }

    @Test
    public void testConnectionReturnsNullIfNoConnectionLinkIdFound() {
        String result = service.testConnection("1715917d-fff8-44a1-af02-ee2cd41a3609", "sales", null);
        assertNull(result);
    }

    @Test
    public void testConnectionWorksByConnectionLink() {
        Connection connection = new Connection();
        connection.setName("sales");

        when(connectionService.findByConnectionLinkId(eq("1715917d-fff8-44a1-af02-ee2cd41a3609")))
            .thenReturn(connection);
        when(queryService.executeQuery(eq(connection), any(FlairQuery.class))).thenReturn("[]");
        String result = service.testConnection("1715917d-fff8-44a1-af02-ee2cd41a3609", "sales", null);

        assertEquals("[]", result);
    }

    @Test
    public void testConnectionWorksByConnection() {
        Connection connection = new Connection();
        connection.setName("connection name");
        when(queryService.executeQuery(any(Connection.class), any(FlairQuery.class))).thenReturn("[]");
        when(connectionMapper.toEntity(any(ConnectionDTO.class))).thenReturn(connection);
        String result = service.testConnection("",
            "sales",
            connection);

        assertEquals("[]", result);
    }
}
