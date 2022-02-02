package com.tasosmartidis.compliancebar.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tasosmartidis.compliancebar.config.properties.HttpClientProperties;
import com.tasosmartidis.compliancebar.features.core.model.exception.ApplicationInitializationException;
import lombok.AllArgsConstructor;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;


@AllArgsConstructor
@Configuration
public class RestTemplateConfig {
	private HttpClientProperties properties;

	@Bean(name = "myCMDB")
	public RestTemplate restTemplate(@Autowired HttpClient httpClient, @Autowired ObjectMapper objectMapper) {
		var restTemplate = new RestTemplate();
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setHttpClient(httpClient);
		restTemplate.setRequestFactory(factory);

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(objectMapper);
		restTemplate.setMessageConverters(List.of(converter));
		return restTemplate;
	}

	@Bean
	public HttpClient httpClient(@Autowired PoolingHttpClientConnectionManager connectionManager,
								 @Autowired RequestConfig requestConfig) {
		return HttpClientBuilder.create()
				.setKeepAliveStrategy((r,c) -> properties.getHttpClientKeepAliveMilliseconds())
				.setConnectionManager(connectionManager)
				.setDefaultRequestConfig(requestConfig)
				.disableConnectionState()
				.build();
	}

	@Bean
	public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(@Autowired SSLConnectionSocketFactory sslSocketFactory) {
		final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("https", sslSocketFactory)
				.build();
		var connectionManager = new PoolingHttpClientConnectionManager(registry);
		connectionManager.setMaxTotal(properties.getHttpClientTotalConnections());
		connectionManager.setDefaultMaxPerRoute(properties.getHttpClientMaxConnectionsPerRoute());
		return connectionManager;
	}

	@Bean
	public SSLConnectionSocketFactory sslConnectionSocketFactory() {
		try {
			KeyStore keyStore = createKeystore(properties.getKeystoreType(), properties.getKeystoreBase64(), properties.getKeystorePassword());
			KeyStore trustStore = createKeystore(properties.getTruststoreType(), properties.getTruststoreBase64(), properties.getTruststorePassword());
			SSLContext sslContext = SSLContextBuilder
					.create()
					.loadKeyMaterial(keyStore, properties.getKeystorePassword().toCharArray())
					.loadTrustMaterial(trustStore, null)
					.build();

			return new SSLConnectionSocketFactory(sslContext,
					new String[] { "TLSv1.2" },
					properties.getSupportedCiphers(),
					SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		}catch (UnrecoverableKeyException | NoSuchAlgorithmException  | KeyStoreException | KeyManagementException e) {
			throw new ApplicationInitializationException(e.getMessage());
		}
	}

	private KeyStore createKeystore(String type, String keystoreBase64, String password) {
		try(InputStream inputStream = new ByteArrayInputStream(keystoreBase64.getBytes(StandardCharsets.UTF_8));
			Base64InputStream keystoreInputStream = new Base64InputStream(inputStream)) {
			var store = KeyStore.getInstance(type);
			store.load(keystoreInputStream, password.toCharArray());
			return store;
		} catch (IOException | KeyStoreException  | CertificateException | NoSuchAlgorithmException e) {
			throw new ApplicationInitializationException(e.getMessage());
		}

	}

	@Bean
	public RequestConfig requestConfig() {
		return RequestConfig.custom()
				.setConnectTimeout(properties.getConnectionTimeoutMilliseconds())
				.setConnectionRequestTimeout(properties.getConnectionRequestTimeoutMilliseconds())
				.build();
	}

	@Bean
	public ObjectMapper objectMapper() {
		var mapper = new ObjectMapper();
		mapper.deactivateDefaultTyping();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return mapper;
	}
}
