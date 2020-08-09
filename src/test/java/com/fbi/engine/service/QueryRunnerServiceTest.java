package com.fbi.engine.service;

import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryService;
import com.fbi.engine.service.cache.CacheMetadata;
import com.fbi.engine.service.cache.QueryParams;
import com.fbi.engine.service.dto.RunQueryResultDTO;
import com.project.bi.query.dto.QueryDTO;
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
public class QueryRunnerServiceTest {

    @Mock
    private ConnectionService connectionService;

    @Mock
    private QueryService queryService;

    private QueryRunnerService service;

    @Before
    public void setUp() throws Exception {
        service = new QueryRunnerService(connectionService, queryService);
    }

    @Test
    public void runQuerySucceeds() {
        when(connectionService.findByConnectionLinkId(eq("linkid"))).thenReturn(new Connection());
        when(queryService.executeQuery(any(QueryParams.class))).thenReturn(new CacheMetadata().setResult("result"));

        RunQueryResultDTO result = service.runQuery(new QueryDTO(), "linkid");

        assertEquals("result", result.getRawResult());
        assertEquals(RunQueryResultDTO.Result.OK, result.getResultCode());
    }

    @Test
    public void runQueryFailsIfConnectionDoesNotExist() {
        RunQueryResultDTO result = service.runQuery(new QueryDTO(), "linkid");

        assertNull(result.getRawResult());
        assertEquals(RunQueryResultDTO.Result.DATASOURCE_NOT_FOUND, result.getResultCode());
    }

    @Test
    public void runQueryFailsIfQueryReturnsEmptyString() {
        when(connectionService.findByConnectionLinkId(eq("linkid"))).thenReturn(new Connection());
        when(queryService.executeQuery(any(QueryParams.class))).thenReturn(new CacheMetadata().setResult(""));

        RunQueryResultDTO result = service.runQuery(new QueryDTO(), "linkid");

        assertNull(result.getRawResult());
        assertEquals(RunQueryResultDTO.Result.INVAILD_QUERY, result.getResultCode());
    }
}
