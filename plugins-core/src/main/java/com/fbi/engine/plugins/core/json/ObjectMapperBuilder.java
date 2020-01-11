package com.fbi.engine.plugins.core.json;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperBuilder {

	private ObjectMapper mapper = new ObjectMapper();

	public ObjectMapperBuilder configure(final ObjectMapper mapper) {
		this.mapper = mapper;
		return this;
	}

	public ObjectMapperBuilder withModule(final Module module) {
		this.mapper.registerModule(module);
		return this;
	}

	public ObjectMapper build() {
		return this.mapper;
	}
}
