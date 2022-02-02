package com.tasosmartidis.mycmdb;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class Test {

	final static String KEYSTORE_PASSWORD = "password";

	static
	{
		System.setProperty("javax.net.ssl.trustStore", "/Users/tasos/dev/compliance-promoter/compliance-bar/src/main/resources/certs/compliance-bar.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", KEYSTORE_PASSWORD);
		System.setProperty("javax.net.ssl.keyStore", "/Users/tasos/dev/compliance-promoter/compliance-bar/src/main/resources/certs/compliance-bar.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", KEYSTORE_PASSWORD);

		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
				new javax.net.ssl.HostnameVerifier() {

					public boolean verify(String hostname,
										  javax.net.ssl.SSLSession sslSession) {
						if (hostname.equals("localhost")) {
							return true;
						}
						return false;
					}
				});
	}

	public static void main(String[] args) {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity("https://localhost:8443/api/configuration/items?minAvailability=3", String.class);
//		ResponseEntity<String> response = restTemplate.getForEntity("https://localhost:8443/api/configuration/items?minAvailability=3", String.class);
		System.out.println(response.getBody());

//		ResponseEntity<String> response2 = restTemplate.getForEntity("https://127.0.0.1:8443/actuator/prometheus", String.class);
//		System.out.println(response2.getBody());

	}
}
