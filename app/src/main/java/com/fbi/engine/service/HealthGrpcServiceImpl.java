package com.fbi.engine.service;

import org.lognet.springboot.grpc.GRpcService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@GRpcService
@ConditionalOnProperty(value = "grpc.enabled", havingValue = "true")
public class HealthGrpcServiceImpl extends AbstractHealthGrpcService {
}
