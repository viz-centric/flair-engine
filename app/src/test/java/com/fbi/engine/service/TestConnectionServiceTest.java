package com.fbi.engine.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryService;
import com.fbi.engine.service.cache.CacheMetadata;
import com.project.bi.query.FlairQuery;

@ExtendWith(MockitoExtension.class)
public class TestConnectionServiceTest {

	private TestConnectionService service;

	@Mock
	private QueryService queryService;

	@BeforeEach
	public void setUp() {
		service = new TestConnectionService(queryService);
	}

	@Test
	public void testConnectionWorksByConnection() {
		Connection connection = new Connection();
		connection.setName("connection name");
		when(queryService.executeQuery(any(Connection.class), any(FlairQuery.class)))
				.thenReturn(new CacheMetadata().setResult("[]"));
		String result = service.testConnection(connection);

		assertEquals("[]", result);
	}
}
