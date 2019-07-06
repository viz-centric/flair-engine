package com.fbi.engine.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Profile("test")
@Service
public class MockHealthGrpcService extends AbstractHealthGrpcService {

    public MockHealthGrpcService() {
        super();
    }
}
