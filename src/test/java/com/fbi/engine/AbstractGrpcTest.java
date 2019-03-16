package com.fbi.engine;

import io.grpc.testing.GrpcCleanupRule;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FbiengineApp.class)
@Transactional
public abstract class AbstractGrpcTest {

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

}
