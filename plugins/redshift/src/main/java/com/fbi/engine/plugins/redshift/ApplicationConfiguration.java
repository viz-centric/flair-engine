package com.fbi.engine.plugins.redshift;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.plugins.core.json.JacksonFactory;
import com.fbi.engine.plugins.core.sql.DriverLoadingStrategy;
import com.fbi.engine.plugins.core.sql.DynamicDriverLoadingStrategy;
import com.flair.bi.compiler.redshift.RedshiftFlairCompiler;
import com.project.bi.query.FlairCompiler;

@Configuration
public class ApplicationConfiguration {

	@Bean
	public ObjectMapper objectMapper() {
		return JacksonFactory.getInstance().getObjectMapper();
	}

	@Bean
	public FlairCompiler compiler() {
		return new RedshiftFlairCompiler();
	}

	@Bean
	public DriverLoadingStrategy strategy() {
		return new DynamicDriverLoadingStrategy();
	}

}
