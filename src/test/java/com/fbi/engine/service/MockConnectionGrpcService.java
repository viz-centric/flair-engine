package com.fbi.engine.service;

import com.fbi.engine.service.mapper.ConnectionDetailsMapper;
import com.fbi.engine.service.mapper.ConnectionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Profile("test")
@Service
public class MockConnectionGrpcService extends AbstractConnectionGrpcService {

    public MockConnectionGrpcService(ConnectionService connectionService, ConnectionTypeService connectionTypeService, TestConnectionService connectionTestService, ConnectionDetailsMapper connectionDetailsMapper, ConnectionMapper connectionMapper, ListTablesService listTablesService) {
        super(connectionService, connectionTypeService, connectionTestService, connectionDetailsMapper, connectionMapper, listTablesService);
    }
}
