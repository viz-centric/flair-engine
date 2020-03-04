package com.fbi.engine.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fbi.engine.service.mapper.ConnectionDetailsMapper;

@Profile("test")
@Service
public class MockConnectionGrpcService extends ConnectionGrpcService {

	public MockConnectionGrpcService(ConnectionService connectionService,
			ConnectionParameterService connectionParameterService, ConnectionTypeService connectionTypeService,
			TestConnectionService connectionTestService, ConnectionDetailsMapper connectionDetailsMapper,
			ListTablesService listTablesService, ConnectionHelperService connectionHelperService) {
		super(connectionService, connectionParameterService, connectionTypeService, connectionTestService,
				connectionDetailsMapper, listTablesService, connectionHelperService);
	}
}
