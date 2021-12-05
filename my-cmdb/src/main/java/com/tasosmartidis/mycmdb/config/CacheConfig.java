package com.tasosmartidis.mycmdb.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Slf4j
@Configuration
@EnableCaching
@AllArgsConstructor
public class CacheConfig extends CachingConfigurerSupport {

	@Bean("ciCache")
	public Cache cache(CacheManager cacheManager) {
		return cacheManager.getCache("ci");
	}

	@Bean
	public CacheManager cacheManager(@Qualifier("client-hazelcast") HazelcastInstance hazelcastInstance) {
		return new HazelcastCacheManager(hazelcastInstance);
	}


	@Bean(name = "client-hazelcast")
	public HazelcastInstance hazelcastInstance(ClientConfig clientConfig) {
		HazelcastInstance hazelcastInstance = null;
		try{
			hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);
		} catch (Exception ex) {
			log.error("Could not connect to hazelcast. Exception: {}", ex.getMessage());
		}
		return hazelcastInstance;
	}

	@Profile({"local"})
	@Bean
	public ClientConfig clientConfigLocal() {
		return createBasicClientConfig();
	}

	private ClientConfig createBasicClientConfig() {
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.getConnectionStrategyConfig()
				.getConnectionRetryConfig()
				.setClusterConnectTimeoutMillis(10000);
		return clientConfig;
	}

	@Profile("test")
	@Bean
	public ClientConfig clientConfigTest() {
		ClientConfig clientConfig = createBasicClientConfig();
		clientConfig.getNetworkConfig()
				.addAddress("mycmdb-hazelcast:5701")
				.setSmartRouting(true);
		return clientConfig;
	}



}
