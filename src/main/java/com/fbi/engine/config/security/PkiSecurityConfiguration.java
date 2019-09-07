package com.fbi.engine.config.security;

import com.fbi.engine.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@EnableWebSecurity
@Order(1)
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "application.authentication.flair-bi.pki", name = "enabled")
public class PkiSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final ApplicationProperties properties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .anyRequest()
            .authenticated()
        .and()
            .x509()
            .subjectPrincipalRegex("CN=(.*?)(?:,|$)")
            .userDetailsService(userDetailsService())
        .and()
            .csrf().disable();

    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return (username -> {
            if (properties.getAuthentication().getFlairBi().getPki().getSubjects().contains(username)) {
                return new User(username, "", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
            } else {
                throw new UsernameNotFoundException("Invalid response");
            }
        });
    }
}
