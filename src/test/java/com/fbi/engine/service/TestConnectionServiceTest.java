package com.fbi.engine.service;

import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryService;
import com.fbi.engine.service.cache.CacheMetadata;
import com.fbi.engine.service.cache.QueryParams;
import com.fbi.engine.service.dto.ConnectionDTO;
import com.fbi.engine.service.mapper.ConnectionMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestConnectionServiceTest {

    private TestConnectionService service;

    @Mock
    private QueryService queryService;
    @Mock
    private ConnectionMapper connectionMapper;

    @Before
    public void setUp() {
        service = new TestConnectionService(queryService);
    }

    @Test
    public void testConnectionWorksByConnection() {
        Connection connection = new Connection();
        connection.setName("connection name");
        when(queryService.executeQuery(any(QueryParams.class))).thenReturn(new CacheMetadata().setResult("[]"));
        when(connectionMapper.toEntity(any(ConnectionDTO.class))).thenReturn(connection);
        String result = service.testConnection(
                connection);

        assertEquals("[]", result);
    }
}
