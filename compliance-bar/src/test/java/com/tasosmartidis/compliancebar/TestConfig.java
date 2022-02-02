package com.tasosmartidis.compliancebar;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TestConfig {

	@Bean
	public RestTemplate restTemplate() {
		var restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor("tasos", "iamauser"));
		return restTemplate;
	}
}
