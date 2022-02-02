package com.tasosmartidis.compliancebar.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static com.tasosmartidis.compliancebar.features.core.service.RateLimitingService.RATE_LIMIT_MAP_NAME;


@Configuration
@Profile({"local"})
public class LocalHazelcastConfig {

	@Bean
	public HazelcastInstance hazelcastInstance(Config config) {
		return Hazelcast.newHazelcastInstance(config);
	}

	@Bean
	public Config config() {
		Config config = new Config();
		// seems that even if we don't specify in config, when I try to get a map with a name hazelcast creates it
		config.addMapConfig(mapConfig("compliance-bar"));
		config.addMapConfig(mapConfig(RATE_LIMIT_MAP_NAME));
		config.getNetworkConfig().setPort(6701);
		return config;
	}

	private MapConfig mapConfig(String mapName) {
		MapConfig mapConfig = new MapConfig(mapName);
		mapConfig.setTimeToLiveSeconds(360);
		mapConfig.setMaxIdleSeconds(20);
		return mapConfig;
	}
}
