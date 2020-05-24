package com.fbi.engine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.query.QueryServiceImpl;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.context.annotation.Profile;

@GRpcService
@Profile("!test")
public class QueryGrpcService extends AbstractQueryGrpcService {
    public QueryGrpcService(ConnectionService connectionService, QueryServiceImpl queryService, ObjectMapper objectMapper, QueryRunnerService queryRunnerService, ConnectionParameterService connectionParameterService, ConnectionHelperService connectionHelperService) {
        super(connectionService, queryService, objectMapper, queryRunnerService, connectionParameterService, connectionHelperService);
    }
}
