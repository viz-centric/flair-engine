package com.fbi.engine;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.netflix.discovery.EurekaClient;

@SpringBootTest(classes = FbiengineApp.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

	@MockBean
	protected EurekaClient eurekaClient;

}
