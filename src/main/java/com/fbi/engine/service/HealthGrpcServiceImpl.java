package com.fbi.engine.service;

import org.lognet.springboot.grpc.GRpcService;
import org.springframework.context.annotation.Profile;

@GRpcService
@Profile("!test")
public class HealthGrpcServiceImpl extends AbstractHealthGrpcService {
}
