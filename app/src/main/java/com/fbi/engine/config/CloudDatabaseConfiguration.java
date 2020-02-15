package com.fbi.engine.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.github.jhipster.config.JHipsterConstants;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@Profile(JHipsterConstants.SPRING_PROFILE_CLOUD)
public class CloudDatabaseConfiguration extends AbstractCloudConfig {

	private static final String CLOUD_CONFIGURATION_HIKARI_PREFIX = "spring.datasource.hikari";

	@Bean
	@ConfigurationProperties(CLOUD_CONFIGURATION_HIKARI_PREFIX)
	public DataSource dataSource(CacheManager cacheManager) {
		log.info("Configuring JDBC datasource from a cloud provider");
		return connectionFactory().dataSource();
	}
}
