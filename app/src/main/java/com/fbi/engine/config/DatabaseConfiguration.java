package com.fbi.engine.config;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.github.jhipster.config.JHipsterConstants;
import io.github.jhipster.config.liquibase.SpringLiquibaseUtil;
import liquibase.integration.spring.SpringLiquibase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableJpaRepositories("com.fbi.engine.repository")
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware", dateTimeProviderRef = "auditingDateTimeProvider")
@EnableTransactionManagement
@Slf4j
@RequiredArgsConstructor
public class DatabaseConfiguration {

	private final Environment env;

	@Bean(name = "auditingDateTimeProvider")
	public DateTimeProvider dateTimeProvider() {
		return () -> Optional.of(OffsetDateTime.now());
	}

	@Bean
	public SpringLiquibase liquibase(@Qualifier("taskExecutor") Executor executor,
			@LiquibaseDataSource ObjectProvider<DataSource> liquibaseDataSource,
			LiquibaseProperties liquibaseProperties, ObjectProvider<DataSource> dataSource,
			DataSourceProperties dataSourceProperties) {

		// If you don't want Liquibase to start asynchronously, substitute by this:
		// SpringLiquibase liquibase =
		// SpringLiquibaseUtil.createSpringLiquibase(liquibaseDataSource.getIfAvailable(),
		// liquibaseProperties, dataSource.getIfUnique(), dataSourceProperties);
		final SpringLiquibase liquibase = SpringLiquibaseUtil.createAsyncSpringLiquibase(this.env, executor,
				liquibaseDataSource.getIfAvailable(), liquibaseProperties, dataSource.getIfUnique(),
				dataSourceProperties);
		liquibase.setChangeLog("classpath:config/liquibase/master.xml");
		liquibase.setContexts(liquibaseProperties.getContexts());
		liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
		// TODO will be uncommented after liquibase is updated
		// liquibase.setLiquibaseSchema(liquibaseProperties.getLiquibaseSchema());
		// liquibase.setLiquibaseTablespace(liquibaseProperties.getLiquibaseTablespace());
		// liquibase.setDatabaseChangeLogLockTable(liquibaseProperties.getDatabaseChangeLogLockTable());
		// liquibase.setDatabaseChangeLogTable(liquibaseProperties.getDatabaseChangeLogTable());
		liquibase.setDropFirst(liquibaseProperties.isDropFirst());
		liquibase.setLabels(liquibaseProperties.getLabels());
		liquibase.setChangeLogParameters(liquibaseProperties.getParameters());
		liquibase.setRollbackFile(liquibaseProperties.getRollbackFile());
		// liquibase.setTestRollbackOnUpdate(liquibaseProperties.isTestRollbackOnUpdate());
		if (env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_NO_LIQUIBASE))) {
			liquibase.setShouldRun(false);
		} else {
			liquibase.setShouldRun(liquibaseProperties.isEnabled());
			log.debug("Configuring Liquibase");
		}
		return liquibase;
	}
}
