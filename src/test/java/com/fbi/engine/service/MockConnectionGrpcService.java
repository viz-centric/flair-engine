package com.fbi.engine.service;

import com.fbi.engine.service.mapper.ConnectionDetailsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Slf4j
@Profile("test")
@Service
public class MockConnectionGrpcService extends AbstractConnectionGrpcService {

    public MockConnectionGrpcService(ConnectionService connectionService, ConnectionParameterService connectionParameterService, ConnectionTypeService connectionTypeService, TestConnectionService connectionTestService, ConnectionDetailsMapper connectionDetailsMapper, ListTablesService listTablesService, ConnectionHelperService connectionHelperService, Environment environment) {
        super(connectionService, connectionParameterService, connectionTypeService, connectionTestService, connectionDetailsMapper, listTablesService, connectionHelperService, environment);
    }
}
