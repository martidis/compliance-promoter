package com.tasosmartidis.compliancebar.config;

import com.tasosmartidis.compliancebar.features.iam.SimpleAuthenticationSuccessHandler;
import com.tasosmartidis.compliancebar.features.iam.UsersService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

@AllArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private AccessDeniedHandler accessDeniedHandler;
	private BCryptPasswordEncoder passwordEncoder;
	private UsersService usersService;
	private SimpleAuthenticationSuccessHandler successHandler;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
			.authorizeRequests()
				.antMatchers( "/login", "/actuator/**", "/vendor/**", "/js/**", "/css/**", "/static/**")
					.permitAll()
				.antMatchers("/**").hasAnyRole("USER", "COMPLIANCE_OFFICER")
					.anyRequest().authenticated()
//				.and()
//				.requiresChannel().anyRequest().requiresSecure()
			.and()
			.formLogin()
				.loginPage("/login")
				.successHandler(successHandler)
				.failureUrl("/login")
				.defaultSuccessUrl("/overview", true)
				.permitAll()
				.and()
				.logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login")
				.permitAll()
				.and()
				.exceptionHandling().accessDeniedHandler(accessDeniedHandler)
			.and()
			.headers()
				.defaultsDisabled()
				.frameOptions().sameOrigin() // if for some reason a page in our server wants to iframe us, we allow them :)
				.xssProtection()
				.and()
				.cacheControl()
				.and()
				.contentSecurityPolicy("form-action 'self'")
				.and()
				.httpStrictTransportSecurity()
				.includeSubDomains(true);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(usersService);
		authProvider.setPasswordEncoder(passwordEncoder);
		auth.authenticationProvider(authProvider);
	}

}
