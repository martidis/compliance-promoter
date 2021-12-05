package com.tasosmartidis.mycmdb.config.interceptor;

import com.tasosmartidis.mycmdb.core.model.exception.TooManyRequestsException;
import com.tasosmartidis.mycmdb.core.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@AllArgsConstructor
@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

	private final RateLimitingService rateLimitingService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		var clientUsername = SecurityContextHolder.getContext().getAuthentication().getName();
		Bucket tokenBucket = rateLimitingService.resolveBucket(clientUsername);
		// I will implement later informing consumers when they can retry
		ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);
		if (!probe.isConsumed()) {
			throw new TooManyRequestsException("Customer " + clientUsername + " exceeded the rate limit");
		}
		return true;
	}

}
