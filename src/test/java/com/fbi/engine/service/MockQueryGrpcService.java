package com.fbi.engine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.query.QueryServiceImpl;
import com.fbi.engine.service.validators.QueryValidator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
public class MockQueryGrpcService extends AbstractQueryGrpcService {
    public MockQueryGrpcService(ConnectionService connectionService, QueryServiceImpl queryService, QueryValidator queryValidator, ObjectMapper objectMapper, QueryRunnerService queryRunnerService, ConnectionParameterService connectionParameterService) {
        super(connectionService, queryService, queryValidator, objectMapper, queryRunnerService, connectionParameterService);
    }
}
