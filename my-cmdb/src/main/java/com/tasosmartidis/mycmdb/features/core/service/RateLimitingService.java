package com.tasosmartidis.mycmdb.features.core.service;

import com.hazelcast.core.HazelcastInstance;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.grid.ProxyManager;
import io.github.bucket4j.grid.hazelcast.Hazelcast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitingService {

	public static final String RATE_LIMIT_MAP_NAME = "rate-limit";

	private final long capacity;
	private final long tokenRefill;

	private final ProxyManager<String> buckets;

	public RateLimitingService(@Autowired HazelcastInstance hazelcastInstance,
							   @Value("${rate.limiting.refill}") long tokenRefill,
							   @Value("${rate.limiting.capacity}") long capacity) {
		this.buckets = Bucket4j.extension(Hazelcast.class)
				.proxyManagerForMap(hazelcastInstance.getMap(RATE_LIMIT_MAP_NAME));
		this.tokenRefill = tokenRefill;
		this.capacity = capacity;
	}


	public Bucket resolveBucket(String authenticatedUser) {
		return buckets.getProxy(authenticatedUser, newBucket());

	}

	private BucketConfiguration newBucket() {
		return Bucket4j.configurationBuilder()
					.addLimit(Bandwidth.classic(capacity, Refill.intervally(tokenRefill, Duration.ofMinutes(1))))
					.build();
	}
}
