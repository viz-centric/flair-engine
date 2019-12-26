package com.fbi.engine.query.abstractfactory;

import org.pf4j.spring.SpringPluginManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class Factory {

	@Bean
	@DependsOn("pluginManager")
	public QueryAbstractFactory createAbstractFactory(SpringPluginManager pluginManager) {
		return new FlyweightQueryAbstractFactory(pluginManager);
	}

}
