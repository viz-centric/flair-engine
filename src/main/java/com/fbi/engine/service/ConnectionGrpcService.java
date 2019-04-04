package com.fbi.engine.service;

import com.fbi.engine.service.mapper.ConnectionDetailsMapper;
import com.fbi.engine.service.mapper.ConnectionMapper;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.context.annotation.Profile;

@GRpcService
@Profile("!test")
public class ConnectionGrpcService extends AbstractConnectionGrpcService {

    public ConnectionGrpcService(ConnectionService connectionService, ConnectionParameterService connectionParameterService, ConnectionTypeService connectionTypeService, TestConnectionService connectionTestService, ConnectionDetailsMapper connectionDetailsMapper, ConnectionMapper connectionMapper, ListTablesService listTablesService) {
        super(connectionService, connectionParameterService, connectionTypeService, connectionTestService, connectionDetailsMapper, connectionMapper, listTablesService);
    }

}
