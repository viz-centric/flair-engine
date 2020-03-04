package com.fbi.engine.config;

import static io.github.jhipster.config.logging.LoggingUtils.addContextListener;
import static io.github.jhipster.config.logging.LoggingUtils.addJsonConsoleAppender;
import static io.github.jhipster.config.logging.LoggingUtils.addLogstashTcpSocketAppender;
import static io.github.jhipster.config.logging.LoggingUtils.setMetricsMarkerLogbackFilter;

import java.util.HashMap;
import java.util.Map;

import org.lognet.springboot.grpc.autoconfigure.GRpcServerProperties;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.LoggerContext;
import io.github.jhipster.config.JHipsterProperties;

/*
 * Configures the console and Logstash log appenders from the app properties
 */
@Configuration
@RefreshScope
public class LoggingConfiguration {

	public LoggingConfiguration(@Value("${spring.application.name}") String appName,
			@Value("${server.port}") String serverPort, JHipsterProperties jHipsterProperties,
			ObjectProvider<BuildProperties> buildProperties, ObjectMapper mapper,
			GRpcServerProperties grpcServerProperties) throws JsonProcessingException {

		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

		Map<String, String> map = new HashMap<>();
		map.put("app_name", appName);
		map.put("app_port", serverPort);
		if (grpcServerProperties.isEnabled()) {
			map.put("grpc_port", String.valueOf(grpcServerProperties.getPort()));
		}
		buildProperties.ifAvailable(it -> map.put("version", it.getVersion()));
		String customFields = mapper.writeValueAsString(map);

		JHipsterProperties.Logging loggingProperties = jHipsterProperties.getLogging();
		JHipsterProperties.Logging.Logstash logstashProperties = loggingProperties.getLogstash();

		if (loggingProperties.isUseJsonFormat()) {
			addJsonConsoleAppender(context, customFields);
		}
		if (logstashProperties.isEnabled()) {
			addLogstashTcpSocketAppender(context, customFields, logstashProperties);
		}
		if (loggingProperties.isUseJsonFormat() || logstashProperties.isEnabled()) {
			addContextListener(context, customFields, loggingProperties);
		}
		if (jHipsterProperties.getMetrics().getLogs().isEnabled()) {
			setMetricsMarkerLogbackFilter(context, loggingProperties.isUseJsonFormat());
		}
	}
}
