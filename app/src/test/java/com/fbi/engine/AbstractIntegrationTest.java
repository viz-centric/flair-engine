package com.fbi.engine;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(classes = FbiengineApp.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

}
