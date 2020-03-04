package com.fbi.engine;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.lognet.springboot.grpc.autoconfigure.GRpcServerProperties;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.fbi.engine.api.FlairFactory;
import com.fbi.engine.config.DefaultProfileUtil;

import io.github.jhipster.config.JHipsterConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@EnableEurekaClient
@SpringBootApplication(exclude = { MongoAutoConfiguration.class, MongoDataAutoConfiguration.class })
@EnableConfigurationProperties({ LiquibaseProperties.class, ApplicationProperties.class })
@Slf4j
@RequiredArgsConstructor
public class FbiengineApp {

	private final Environment env;

	/**
	 * Initializes fbiengine.
	 * <p>
	 * Spring profiles can be configured with a program arguments
	 * --spring.profiles.active=your-active-profile
	 * <p>
	 * You can find more information on how profiles work with JHipster on <a href=
	 * "https://jhipster.github.io/profiles/">https://jhipster.github.io/profiles/</a>.
	 */
	@PostConstruct
	public void initApplication() {
		Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
		if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)
				&& activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)) {
			log.error("You have misconfigured your application! It should not run "
					+ "with both the 'dev' and 'prod' profiles at the same time.");
		}
		if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)
				&& activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_CLOUD)) {
			log.error("You have misconfigured your application! It should not "
					+ "run with both the 'dev' and 'cloud' profiles at the same time.");
		}
	}

	/**
	 * Main method, used to run the application.
	 *
	 * @param args the command line arguments
	 * @throws UnknownHostException if the local host name could not be resolved
	 *                              into an address
	 */
	public static void main(String[] args) throws UnknownHostException {
		SpringApplication app = new SpringApplication(FbiengineApp.class);
		DefaultProfileUtil.addDefaultProfile(app);
		Environment env = app.run(args).getEnvironment();

		if (Arrays.asList(env.getActiveProfiles()).contains("grpc")) {
			String protocol = "grpc";
			log.info(
					"\n----------------------------------------------------------\n\t"
							+ "Application '{}' is running! Access URLs:\n\t" + "Local: \t\t{}://localhost:{}\n\t"
							+ "External: \t{}://{}:{}\n\t"
							+ "Profile(s): \t{}\n----------------------------------------------------------",
					env.getProperty("spring.application.name"), protocol, env.getProperty("grpc.port"), protocol,
					InetAddress.getLocalHost().getHostAddress(), env.getProperty("grpc.port"), env.getActiveProfiles());
		} else {
			String protocol = "http";
			if (env.getProperty("server.ssl.key-store") != null) {
				protocol = "https";
			}
			log.info(
					"\n----------------------------------------------------------\n\t"
							+ "Application '{}' is running! Access URLs:\n\t" + "Local: \t\t{}://localhost:{}\n\t"
							+ "External: \t{}://{}:{}\n\t"
							+ "Profile(s): \t{}\n----------------------------------------------------------",
					env.getProperty("spring.application.name"), protocol, env.getProperty("server.port"), protocol,
					InetAddress.getLocalHost().getHostAddress(), env.getProperty("server.port"),
					env.getActiveProfiles());

		}
	}

	@Bean
	public SpringPluginManager pluginManager() {
		return new SpringPluginManager();
	}

	@Bean
	public ApplicationRunner run() {
		return new ApplicationRunner() {

			@Autowired
			private SpringPluginManager springPluginManager;

			@Override
			public void run(ApplicationArguments args) throws Exception {
				final List<FlairFactory> plugins = springPluginManager.getExtensions(FlairFactory.class);
				log.info(String.format("Number of plugins found: %d", plugins.size()));
				plugins.forEach(x -> {
					log.info("Plugin loaded: {}", x.getExtensionId());
				});
			}
		};
	}

}
