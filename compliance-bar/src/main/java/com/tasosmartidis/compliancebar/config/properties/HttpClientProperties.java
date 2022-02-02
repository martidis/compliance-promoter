package com.tasosmartidis.compliancebar.config.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class HttpClientProperties {

	@Value("${httpclient.keystore.type}")
	private String keystoreType;
	@Value("${httpclient.keystore.base64Value}")
	private String keystoreBase64;
	@Value("${httpclient.keystore.password}")
	private String keystorePassword;
	@Value("${httpclient.truststore.type}")
	private String truststoreType;
	@Value("${httpclient.truststore.base64Value}")
	private String truststoreBase64;
	@Value("${httpclient.truststore.password}")
	private String truststorePassword;

	@Value("${httpclient.pool.size}")
	private int httpClientPoolSize;
	@Value("${httpclient.keepalive}")
	private int httpClientKeepAliveMilliseconds;
	@Value("${httpclient.connectionTimeoutMilliseconds}")
	private int connectionTimeoutMilliseconds;
	@Value("${httpclient.connectionRequestTimeoutMilliseconds}")
	private int connectionRequestTimeoutMilliseconds;
	@Value("${httpclient.ciphers}")
	private String[] supportedCiphers;
	@Value("${httpclient.totalConnections}")
	private int httpClientTotalConnections;
	@Value("${httpclient.maxConnectionsPerRoute}")
	private int httpClientMaxConnectionsPerRoute;

}
