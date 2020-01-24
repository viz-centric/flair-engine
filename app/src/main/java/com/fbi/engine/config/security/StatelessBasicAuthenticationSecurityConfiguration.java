package com.fbi.engine.config.security;

import com.fbi.engine.ApplicationProperties;
import com.fbi.engine.security.AuthoritiesConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Order(3)
@ConditionalOnProperty(prefix = "application.authentication.flair-bi.basic-authentication", name = "enabled")
public class StatelessBasicAuthenticationSecurityConfiguration extends WebSecurityConfigurerAdapter {

	private final ApplicationProperties applicationProperties;

	@Autowired
	public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
		for (ApplicationProperties.Authentication.FlairBi.BasicAuthentication.Credentials cred : applicationProperties
				.getAuthentication().getFlairBi().getBasicAuthentication().getCredentials()) {

			String[] roles = Optional.ofNullable(cred.getRoles()).filter(x -> !x.isEmpty())
					.map(x -> x.toArray(new String[0])).orElse(new String[] { "ROLE_USER" });
			auth.inMemoryAuthentication().withUser(cred.getUsername()).password(cred.getPassword()).authorities(roles);
		}
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.authorizeRequests().antMatchers("/api/register").permitAll().antMatchers("/api/activate").permitAll()
				.antMatchers("/api/authenticate").permitAll().antMatchers("/api/account/reset_password/init")
				.permitAll().antMatchers("/api/account/reset_password/finish").permitAll()
				.antMatchers("/api/profile-info").permitAll().antMatchers("/api/**").authenticated()
				.antMatchers("/v2/api-docs/**").permitAll().antMatchers("/swagger-resources/configuration/ui")
				.permitAll().antMatchers("/swagger-ui/index.html").hasAuthority(AuthoritiesConstants.ADMIN).and()
				.httpBasic().realmName("FBI-ENGINE-REALM")
				.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()).and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf().disable();
//        @formatter:on

	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
	}

	static class CustomBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

		@Override
		public void afterPropertiesSet() {
			setRealmName("FBI-ENGINE-REALM");
			super.afterPropertiesSet();
		}

		@Override
		public void commence(HttpServletRequest request, HttpServletResponse response,
				AuthenticationException authException) throws IOException {
			// Authentication failed, send error response.
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName() + "");

			PrintWriter writer = response.getWriter();
			writer.println("HTTP Status 401 : " + authException.getMessage());
		}
	}

}
