package com.fbi.engine.plugins.postgres;

import org.pf4j.Extension;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.api.Connection;
import com.fbi.engine.api.DataSourceDriver;
import com.fbi.engine.api.FlairFactory;
import com.fbi.engine.api.QueryExecutor;
import com.fbi.engine.plugins.core.sql.DriverLoadingStrategy;
import com.project.bi.query.FlairCompiler;

public class PostgresPlugin extends SpringPlugin {

	public PostgresPlugin(PluginWrapper wrapper) {
		super(wrapper);
	}

	@Override
	protected ApplicationContext createApplicationContext() {
		final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
		applicationContext.register(ApplicationConfiguration.class);
		applicationContext.refresh();
		return applicationContext;
	}

	@Extension
	public static class SpringPlugin implements FlairFactory {

		@Autowired
		private ObjectMapper mapper;
		@Autowired
		private DriverLoadingStrategy strategy;
		@Autowired
		private FlairCompiler compiler;

		public FlairCompiler getCompiler() {
			return compiler;
		}

		public QueryExecutor getExecutor(Connection connection, DataSourceDriver driver) {
			return new PostgresQueryExecutor(strategy, connection, mapper, driver);
		}

	}

}
