package com.fbi.engine.plugins.postgres;

import java.sql.ResultSet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fbi.engine.plugins.core.ResultSetSerializer;
import com.fbi.engine.plugins.core.sql.DriverLoadingStrategy;
import com.fbi.engine.plugins.core.sql.DynamicDriverLoadingStrategy;
import com.flair.bi.compiler.postgres.PostgresFlairCompiler;
import com.project.bi.query.FlairCompiler;

@Configuration
public class ApplicationConfiguration {

	@Bean
	public DriverLoadingStrategy strategy() {
		return new DynamicDriverLoadingStrategy();
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper obj = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addSerializer(ResultSet.class, new ResultSetSerializer());
		obj.registerModule(module);
		return obj;
	}

	@Bean
	public FlairCompiler compiler() {
		return new PostgresFlairCompiler();
	}

}
