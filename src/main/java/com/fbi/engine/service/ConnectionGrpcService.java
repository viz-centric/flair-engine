package com.fbi.engine.service;

import com.fbi.engine.service.mapper.ConnectionDetailsMapper;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@GRpcService
@Profile("!test")
public class ConnectionGrpcService extends AbstractConnectionGrpcService {

    public ConnectionGrpcService(ConnectionService connectionService, ConnectionParameterService connectionParameterService, ConnectionTypeService connectionTypeService, TestConnectionService connectionTestService, ConnectionDetailsMapper connectionDetailsMapper, ListTablesService listTablesService, ConnectionHelperService connectionHelperService, Environment environment) {
        super(connectionService, connectionParameterService, connectionTypeService, connectionTestService, connectionDetailsMapper, listTablesService, connectionHelperService, environment);
    }

}
