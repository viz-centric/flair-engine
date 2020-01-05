package com.fbi.engine.plugins.kafka;

import org.pf4j.Extension;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbi.engine.api.DataSourceConnection;
import com.fbi.engine.api.DataSourceDriver;
import com.fbi.engine.api.FlairFactory;
import com.fbi.engine.api.QueryExecutor;
import com.project.bi.query.FlairCompiler;

public class KafkaPlugin extends SpringPlugin {

	public KafkaPlugin(PluginWrapper wrapper) {
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
		private FlairCompiler compiler;
		@Autowired
		private RestTemplate restTemplate;

		public FlairCompiler getCompiler() {
			return compiler;
		}

		public QueryExecutor getExecutor(DataSourceConnection connection, DataSourceDriver driver) {
			return new KafkaQueryExecutor(restTemplate, compiler, mapper, connection);
		}

		@Override
		public String getExtensionId() {
			return "com.fbi.engine.query.factory.impl.KafkaFlairFactory";
		}

		@Override
		public String getDescription() {
			return "Extension enabling connection towards Kafka";
		}

	}

}
