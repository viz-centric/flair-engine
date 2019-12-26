package com.fbi.engine.plugins.hello;

import org.pf4j.Extension;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.fbi.engine.api.DataSourceConnection;
import com.fbi.engine.api.DataSourceDriver;
import com.fbi.engine.api.FlairFactory;
import com.fbi.engine.api.QueryExecutor;
import com.project.bi.query.FlairCompiler;

public class HelloPlugin extends SpringPlugin {

	public HelloPlugin(PluginWrapper wrapper) {
		super(wrapper);
	}

	@Override
	protected ApplicationContext createApplicationContext() {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
		applicationContext.register(ApplicationConfiguration.class);
		applicationContext.refresh();
		return applicationContext;
	}

	@Override
	public void start() {
		log.info("Spring Sample plugin.start()");
	}

	@Override
	public void stop() {
		log.info("Spring Sample plugin.stop()");
		super.stop(); // to close applicationContext
	}

	@Extension
	public static class SpringPlugin implements FlairFactory {

		@Autowired
		private FlairCompiler compiler;

		@Autowired
		private QueryExecutorFactory factory;

		public FlairCompiler getCompiler() {
			return compiler;
		}

		public QueryExecutor getExecutor(DataSourceConnection connection, DataSourceDriver driver) {
			return factory.createQueryExecutor(connection, driver);
		}

	}

}
