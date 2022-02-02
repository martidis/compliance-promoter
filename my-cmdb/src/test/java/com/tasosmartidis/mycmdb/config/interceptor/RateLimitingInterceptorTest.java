package com.tasosmartidis.mycmdb.config.interceptor;


import com.tasosmartidis.mycmdb.features.core.model.exception.TooManyRequestsException;
import com.tasosmartidis.mycmdb.features.core.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class RateLimitingInterceptorTest {

	@InjectMocks
	RateLimitingInterceptor rateLimitInterceptor;

	@Mock
	RateLimitingService rateLimitingServiceMock;
	@MockBean
	HttpServletRequest requestMock;
	@MockBean
	HttpServletResponse responseMock;
	@MockBean
	Bucket bucketMock;
	@MockBean
	ConsumptionProbe consumptionProbeMock;

	@BeforeEach
	public void setup() {
		Authentication authentication = Mockito.mock(Authentication.class);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getName()).thenReturn("whoami");
		SecurityContextHolder.setContext(securityContext);
	}

	@Test
	void preHandle_WhenLimitNotExceeded_returnsTrue() {
		when(rateLimitingServiceMock.resolveBucket(anyString())).thenReturn(bucketMock);
		when(bucketMock.tryConsumeAndReturnRemaining(anyLong())).thenReturn(consumptionProbeMock);
		when(consumptionProbeMock.isConsumed()).thenReturn(true);

		rateLimitInterceptor.preHandle(requestMock, responseMock, null);
	}

	@Test
	void preHandle_WhenLimitExceeded_ThrowsException() {
		when(rateLimitingServiceMock.resolveBucket(anyString())).thenReturn(bucketMock);
		when(bucketMock.tryConsumeAndReturnRemaining(anyLong())).thenReturn(consumptionProbeMock);
		when(consumptionProbeMock.isConsumed()).thenReturn(false);

		Assertions.assertThrows(TooManyRequestsException.class, () -> {
			rateLimitInterceptor.preHandle(requestMock, responseMock, null);
		});
	}
}
