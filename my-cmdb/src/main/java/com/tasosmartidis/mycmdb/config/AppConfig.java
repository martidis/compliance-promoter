package com.tasosmartidis.mycmdb.config;

import com.tasosmartidis.mycmdb.config.interceptor.RateLimitingInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AllArgsConstructor
@Configuration
public class AppConfig implements WebMvcConfigurer {
	private RateLimitingInterceptor rateLimitInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(rateLimitInterceptor).addPathPatterns("/api/**");
	}
}
