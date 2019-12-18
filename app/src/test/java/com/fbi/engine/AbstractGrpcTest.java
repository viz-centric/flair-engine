package com.fbi.engine;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import io.grpc.testing.GrpcCleanupRule;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FbiengineApp.class)
@Transactional
public abstract class AbstractGrpcTest {

	@Rule
	public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

}
