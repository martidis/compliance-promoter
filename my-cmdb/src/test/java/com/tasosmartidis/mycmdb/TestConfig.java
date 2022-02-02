package com.tasosmartidis.mycmdb;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
public class TestConfig {

	@Bean
	public RestTemplate rt() throws Exception {
		char[] password = "password".toCharArray();
		SSLContext sslContext = SSLContextBuilder.create()
				.loadKeyMaterial(keyStore("classpath:compliance-bar.jks", password), password)
				.loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
		HttpClient client = HttpClients.custom().setSSLContext(sslContext).build();

		return  new RestTemplateBuilder()
				.requestFactory(() -> new HttpComponentsClientHttpRequestFactory(client))
				.build();
	}

	private KeyStore keyStore(String file, char[] password) throws Exception {
		KeyStore keyStore = KeyStore.getInstance("JKS");
		File key = ResourceUtils.getFile(file);
		try (InputStream in = new FileInputStream(key)) {
			keyStore.load(in, password);
		}
		return keyStore;
	}

}
