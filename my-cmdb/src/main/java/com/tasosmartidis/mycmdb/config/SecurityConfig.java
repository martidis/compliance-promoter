package com.tasosmartidis.mycmdb.config;

import com.tasosmartidis.mycmdb.iam.ClientsService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private ClientsService clientsService;

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.authorizeRequests()
				.antMatchers("/actuator/**").hasRole("OPS")
				.antMatchers("/**").hasRole("USER")
				.anyRequest().authenticated()
				.and()
				.x509()
					.subjectPrincipalRegex("CN=(.*?)(?:,|$)")
					.userDetailsService(clientsService)
				.and()
				.requiresChannel().anyRequest().requiresSecure()
				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.csrf().disable();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(clientsService);
		auth.authenticationProvider(authProvider);
	}
}
