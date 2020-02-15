package com.fbi.engine.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryService;
import com.fbi.engine.service.cache.CacheMetadata;
import com.fbi.engine.service.cache.CacheParams;
import com.fbi.engine.service.dto.RunQueryResultDTO;
import com.project.bi.query.FlairQuery;
import com.project.bi.query.dto.QueryDTO;

@ExtendWith(MockitoExtension.class)
public class QueryRunnerServiceTest {

	@Mock
	private ConnectionService connectionService;

	@Mock
	private QueryService queryService;

	private QueryRunnerService service;

	@BeforeEach
	public void setUp() throws Exception {
		service = new QueryRunnerService(connectionService, queryService);
	}

	@Test
	public void runQuerySucceeds() {
		when(connectionService.findByConnectionLinkId(eq("linkid"))).thenReturn(new Connection());
		when(queryService.executeQuery(any(Connection.class), any(FlairQuery.class), any(CacheParams.class)))
				.thenReturn(new CacheMetadata().setResult("result"));

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
		when(queryService.executeQuery(any(Connection.class), any(FlairQuery.class), any(CacheParams.class)))
				.thenReturn(new CacheMetadata().setResult(""));

		RunQueryResultDTO result = service.runQuery(new QueryDTO(), "linkid");

		assertNull(result.getRawResult());
		assertEquals(RunQueryResultDTO.Result.INVAILD_QUERY, result.getResultCode());
	}
}
