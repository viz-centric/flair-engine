package com.fbi.engine.plugins.core.json;

import java.sql.ResultSet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.plugins.core.ResultSetSerializer;

public class JacksonFactory {

	private static final JacksonFactory INSTANCE = new JacksonFactory();

	private JacksonFactory() {
	}

	public static JacksonFactory getInstance() {
		return INSTANCE;
	}

	public ObjectMapper getObjectMapper() {
		return new ObjectMapperBuilder()
				.withModule(
						new SimpleModuleBuilder()
						.withSerializer(ResultSet.class, new ResultSetSerializer()).build())
				.build();
	}

}
