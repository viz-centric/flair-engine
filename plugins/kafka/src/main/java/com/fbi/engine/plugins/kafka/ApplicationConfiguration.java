package com.fbi.engine.plugins.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.plugins.core.json.JacksonFactory;
import com.flair.bi.compiler.postgres.PostgresFlairCompiler;
import com.project.bi.query.FlairCompiler;

@Configuration
public class ApplicationConfiguration {

	@Bean
	public ObjectMapper objectMapper() {
		return JacksonFactory.getInstance().getObjectMapper();
	}

	@Bean
	public FlairCompiler compiler() {
		return new PostgresFlairCompiler();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
