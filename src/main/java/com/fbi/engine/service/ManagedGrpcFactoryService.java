package com.fbi.engine.service;

import com.flair.bi.messages.CacheServiceGrpc;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractStub;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagedGrpcFactoryService {

    private final EurekaClient client;

    public ManagedChannel getManagedChannel(String eurekaName) {
        final InstanceInfo instanceInfo = client.getNextServerFromEureka(eurekaName, false);
        return ManagedChannelBuilder.forAddress(instanceInfo.getIPAddr(), instanceInfo.getPort())
                .usePlaintext()
                .build();
    }
}
