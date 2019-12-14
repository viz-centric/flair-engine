package com.fbi.engine.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


@Profile("test")
@Service
public class MockHealthGrpcService extends AbstractHealthGrpcService {

    public MockHealthGrpcService() {
        super();
    }
}
