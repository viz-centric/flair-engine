package com.fbi.engine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.query.QueryServiceImpl;
import com.fbi.engine.service.validators.QueryValidator;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@GRpcService
@ConditionalOnProperty(value = "grpc.enabled", havingValue = "true")
public class QueryGrpcService extends AbstractQueryGrpcService {
    public QueryGrpcService(ConnectionService connectionService, QueryServiceImpl queryService,
            QueryValidator queryValidator, ObjectMapper objectMapper, QueryRunnerService queryRunnerService,
            ConnectionParameterService connectionParameterService, ConnectionHelperService connectionHelperService) {
        super(connectionService, queryService, queryValidator, objectMapper, queryRunnerService,
                connectionParameterService, connectionHelperService);
    }
}
