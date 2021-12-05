package com.tasosmartidis.mycmdb;

import com.tasosmartidis.mycmdb.core.model.api.ConfigurationItemResponse;
import com.tasosmartidis.mycmdb.core.repository.ConfigurationItemsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyCmdbCacheTests {

	@LocalServerPort
	Integer port;

	@SpyBean
	ConfigurationItemsRepository repositorySpy;

	@Autowired
	RestTemplate restTemplate;

	@Qualifier("ciRepo")
	@Autowired
	CrudRepository crudRepository;

	@Autowired
	CacheManager cacheManager;

	Cache cache;

	@BeforeEach
	public void beforeEach() {
		cache = cacheManager.getCache("ci");
		cache.clear();
		reset(repositorySpy);
	}

	@Test
	void retrievingAllConfigurationItems_WhenCalledTwiceWithSameAIC_ValuesForAICPresentInCacheAndRepositoryCalledOnce() {
		assertNull(cache.get("ci_333"));
		var entitiesResponse1 = restTemplate.exchange("https://localhost:" + port + "/api/configuration/items?minAvailability=3&minIntegrity=3&minConfidentiality=3", HttpMethod.GET, null, new ParameterizedTypeReference<List<ConfigurationItemResponse>>() {});
		var entitiesResponse2 = restTemplate.exchange("https://localhost:" + port + "/api/configuration/items?minAvailability=3&minIntegrity=3&minConfidentiality=3", HttpMethod.GET, null, new ParameterizedTypeReference<List<ConfigurationItemResponse>>() {});

		assertNotNull(cache.get("ci_333"));
		assertNotNull(cache.get("ci_333").get());

		verify(repositorySpy, times(1)).findConfigurationEntities(3,3,3);

		assertTrue(entitiesResponse1.getBody().equals(entitiesResponse2.getBody()));
	}

	@Test
	void retrievingAllConfigurationItems_WhenCalledTwiceWithDifferentAIC_RepositoryCalledOnceForEachAndResponsesDiffer() {
		var entitiesResponse1 = restTemplate.exchange("https://localhost:" + port + "/api/configuration/items?minAvailability=3&minIntegrity=3&minConfidentiality=3", HttpMethod.GET, null, new ParameterizedTypeReference<List<ConfigurationItemResponse>>() {});
		var entitiesResponse2 = restTemplate.exchange("https://localhost:" + port + "/api/configuration/items?minAvailability=2&minIntegrity=2&minConfidentiality=2", HttpMethod.GET, null, new ParameterizedTypeReference<List<ConfigurationItemResponse>>() {});

		verify(repositorySpy, times(1)).findConfigurationEntities(3,3,3);
		verify(repositorySpy, times(1)).findConfigurationEntities(2,2,2);

		assertFalse(entitiesResponse1.equals(entitiesResponse2));
	}

	@Test
	void updateConfigurationItem_WhenCIUpdatedAIC_CacheForAICIsInvalidated() throws Exception {
		var cid = "f1717966-07e0-44f4-b70d-289d22a5097d";
		var entity = TestUtils.createConfigurationItemEntityStub(cid, "contain-app", 3,3,3, "somehow-owner", "compare@gmail.com");
		crudRepository.save(entity);

		// query aic 333 to have them in cache
		restTemplate.exchange("https://localhost:" + port + "/api/configuration/items?minAvailability=3&minIntegrity=3&minConfidentiality=3", HttpMethod.GET, null, new ParameterizedTypeReference<List<ConfigurationItemResponse>>() {});
		assertNotNull(cache.get("ci_333"));
		assertNotNull(cache.get("ci_333").get());

		// update entity with aic 333 (changing the aic) to invalidate the cache
		var updateRequest = TestUtils.createConfigurationItemRequestStub(
				"wide-app", 3,3,2, "somehow-owner", "compare@gmail.com");
		var httpEntity = new HttpEntity(updateRequest);
		restTemplate.exchange("https://localhost:" + port + "/api/configuration/items/" + cid, HttpMethod.PUT, httpEntity, ConfigurationItemResponse.class);
		assertNull(cache.get("ci_333"));

		// query 333 again. will not be served from cache
		restTemplate.exchange("https://localhost:" + port + "/api/configuration/items?minAvailability=3&minIntegrity=3&minConfidentiality=3", HttpMethod.GET, null, new ParameterizedTypeReference<List<ConfigurationItemResponse>>() {});
		verify(repositorySpy, times(2)).findConfigurationEntities(3,3,3);

	}

	@Test
	void updateConfigurationItem_WhenCIUpdatedButNotTheAIC_CacheForAICNotInvalidated() throws Exception {
		var cid = "f1717966-07e0-44f4-b70d-289d22a5097d";
		var entity = TestUtils.createConfigurationItemEntityStub(cid, "contain-app", 3,3,3, "somehow-owner", "compare@gmail.com");
		crudRepository.save(entity);

		// query aic 333 to have them in cache
		restTemplate.exchange("https://localhost:" + port + "/api/configuration/items?minAvailability=3&minIntegrity=3&minConfidentiality=3", HttpMethod.GET, null, new ParameterizedTypeReference<List<ConfigurationItemResponse>>() {});
		assertNotNull(cache.get("ci_333"));
		assertNotNull(cache.get("ci_333").get());

		// update entity with aic 333 (not changing the aic). should not invalidate the cache
		var updateRequest = TestUtils.createConfigurationItemRequestStub(
				"wide-app", 3,3,3, "somehow-owner", "compare@gmail.com");
		var httpEntity = new HttpEntity(updateRequest);
		restTemplate.exchange("https://localhost:" + port + "/api/configuration/items/" + cid, HttpMethod.PUT, httpEntity, ConfigurationItemResponse.class);
		assertNotNull(cache.get("ci_333"));

		// query 333 again. will be served from cache
		restTemplate.exchange("https://localhost:" + port + "/api/configuration/items?minAvailability=3&minIntegrity=3&minConfidentiality=3", HttpMethod.GET, null, new ParameterizedTypeReference<List<ConfigurationItemResponse>>() {});
		verify(repositorySpy, times(1)).findConfigurationEntities(3,3,3);

	}

	@Test
	void updateConfigurationItem_WhenCIUpdated_CacheForOtherAICIsNotInvalidated() throws Exception {
		var cid = "f1717966-07e0-44f4-b70d-289d22a5097d";
		var entity = TestUtils.createConfigurationItemEntityStub(cid, "contain-app", 2,2,2, "somehow-owner", "compare@gmail.com");
		crudRepository.save(entity);

		// query aic 333 to have them in cache
		restTemplate.exchange("https://localhost:" + port + "/api/configuration/items?minAvailability=3&minIntegrity=3&minConfidentiality=3", HttpMethod.GET, null, new ParameterizedTypeReference<List<ConfigurationItemResponse>>() {});
		assertNotNull(cache.get("ci_333"));
		assertNotNull(cache.get("ci_333").get());

		// update entity with aic 222. should not invalidate aic 333 cache
		var updateRequest = TestUtils.createConfigurationItemRequestStub(
				"wide-app", 2,2,2, "somehow-owner", "compare@gmail.com");
		var httpEntity = new HttpEntity(updateRequest);
		restTemplate.exchange("https://localhost:" + port + "/api/configuration/items/" + cid, HttpMethod.PUT, httpEntity, ConfigurationItemResponse.class);
		assertNotNull(cache.get("ci_333"));
		assertNull(cache.get("ci_222"));

		// query 333 again. should serve from cache
		restTemplate.exchange("https://localhost:" + port + "/api/configuration/items?minAvailability=3&minIntegrity=3&minConfidentiality=3", HttpMethod.GET, null, new ParameterizedTypeReference<List<ConfigurationItemResponse>>() {});
		verify(repositorySpy, times(1)).findConfigurationEntities(3,3,3);
	}

	@Test
	void deleteConfigurationItem_WhenItWasCached_InvalidatesCache() throws Exception {
		var cid = "bdb8a92e-3f35-4423-a7ed-db80c3313d7a";
		var entity = TestUtils.createConfigurationItemEntityStub(cid, "improve-app", 3, 3, 3, "owner", "t@gmail.com");
		crudRepository.save(entity);

		// query aic 333 to pupulate the cache
		assertNull(cache.get("ci_333"));
		restTemplate.exchange("https://localhost:" + port + "/api/configuration/items?minAvailability=3&minIntegrity=3&minConfidentiality=3", HttpMethod.GET, null, new ParameterizedTypeReference<List<ConfigurationItemResponse>>() {});
		assertNotNull(cache.get("ci_333"));
		assertNotNull(cache.get("ci_333").get());

		// remove ci with aic 333
		restTemplate.exchange("https://localhost:" + port + "/api/configuration/items/" + cid, HttpMethod.DELETE, new HttpEntity(null), String.class);

		// assert cache for aic 333 is evicted
		assertNull(cache.get("ci_333"));
	}

}
