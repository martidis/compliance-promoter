package com.tasosmartidis.mycmdb.core.service;

import com.tasosmartidis.mycmdb.core.model.api.ConfigurationItemRequest;
import com.tasosmartidis.mycmdb.core.model.api.ConfigurationItemResponse;
import com.tasosmartidis.mycmdb.core.model.entity.ConfigurationItemEntity;
import com.tasosmartidis.mycmdb.core.model.exception.NonExistentConfigurationItemException;
import com.tasosmartidis.mycmdb.core.repository.ConfigurationItemsRepository;
import io.micrometer.core.instrument.Metrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@CacheConfig(cacheNames = "ci")
public class ConfigurationItemsService {

	private final ConfigurationItemsRepository repository;
	private final Cache ciCache;

	public ConfigurationItemsService(ConfigurationItemsRepository configurationItemsRepository,
									 @Qualifier("ciCache") Cache cache) {
		this.repository = configurationItemsRepository;
		this.ciCache = cache;
	}

	final Function<ConfigurationItemEntity, ConfigurationItemResponse> entityToResponse = e ->
			new ConfigurationItemResponse(
				e.getId(),
				e.getName(),
				e.getAvailabilityRating(),
				e.getIntegrityRating(),
				e.getConfidentialityRating(),
				e.getSystemOwner(),
				e.getTeamEmail());

	final BiFunction<String, ConfigurationItemRequest, ConfigurationItemEntity> requestToEntity = (s, r) ->
			new ConfigurationItemEntity(
					s,
					r.getConfigurationItemName(),
					r.getAvailabilityRating(),
					r.getIntegrityRating(),
					r.getConfidentialityRating(),
					r.getSystemOwner(),
					r.getTeamEmail());

	public ConfigurationItemResponse createConfigurationItem(ConfigurationItemRequest request) {
		log.debug("Creating configuration item entity");
		var entity = requestToEntity.apply(UUID.randomUUID().toString(), request);
		var persistedEntity = repository.save(entity);
		log.debug("Created configuration item entity with id {}", persistedEntity.getId());
		return entityToResponse.apply(persistedEntity);
	}

	public List<ConfigurationItemResponse> retrieveConfigurationItems(Integer minAvailability, Integer minIntegrity, Integer minConfidentiality) {
		Metrics.counter("mycmdb.service.retrieveConfigurationItems.total").increment();
		log.debug("Retrieving configuration item entities with min aic {}{}{}", minAvailability, minIntegrity, minConfidentiality);

		var cachedResponse = ciCache.get(getCacheKey(minAvailability, minIntegrity, minConfidentiality), List.class);
		if(cachedResponse != null) {
			log.debug("Items with min aic {}{}{} served from cache", minAvailability, minIntegrity, minConfidentiality);
			return cachedResponse;
		}

		Metrics.counter("mycmdb.service.retrieveConfigurationItems.cache.miss").increment();
		var response = repository.findConfigurationEntities(minAvailability, minIntegrity, minConfidentiality)
				.parallelStream()
				.map(entityToResponse)
				.collect(Collectors.toUnmodifiableList());
		ciCache.putIfAbsent(getCacheKey(minAvailability, minIntegrity, minConfidentiality), response);
		log.debug("Items with min aic {}{}{} served from db", minAvailability, minIntegrity, minConfidentiality);
		return response;
	}

	public ConfigurationItemResponse retrieveConfigurationItem(String configurationItem) {
		log.debug("Retrieve configuration item entity with id {}",configurationItem);
		var entity = repository.findConfigurationItemEntityById(configurationItem);
		if(entity == null) {
			throw new NonExistentConfigurationItemException("Configuration item " + configurationItem + " not found");
		}
		log.debug("Retrieved configuration item entity with id {}",configurationItem);
		return entityToResponse.apply(entity);
	}

	@Transactional
	public void updateConfigurationItem(String configurationItem, ConfigurationItemRequest updatedConfigurationItem) {
		log.debug("Update configuration item entity with id {}",configurationItem);

		var entity = repository.findConfigurationItemEntityById(configurationItem);
		if(entity == null) {
			throw new NonExistentConfigurationItemException("Configuration item " + configurationItem + " not found");
		}

		// if aic values did not change, don't evict for the aic, we might have large response saved there
		// if update fails the eviction is kind of without reason. if I do after the update, I can't compare with retrieved entity,
		// I have to keep the values prior to the change. don't feel like doing that
		if(aicChanged(entity, updatedConfigurationItem)) {
			ciCache.evictIfPresent(getCacheKey(entity.getAvailabilityRating(), entity.getIntegrityRating(), entity.getConfidentialityRating()));
			log.debug("Evicted cache record for min aic {}{}{}", entity.getAvailabilityRating(), entity.getIntegrityRating(), entity.getConfidentialityRating());
		}
		var toUpdate = requestToEntity.apply(entity.getId(), updatedConfigurationItem);
		repository.save(toUpdate);
		log.debug("Updated configuration item entity with id {}",configurationItem);
	}

	private boolean aicChanged(ConfigurationItemEntity entity, ConfigurationItemRequest updatedConfigurationItem) {
		return ! (entity.getAvailabilityRating().equals(updatedConfigurationItem.getAvailabilityRating()) &&
				entity.getIntegrityRating().equals(updatedConfigurationItem.getIntegrityRating()) &&
				entity.getConfidentialityRating().equals(updatedConfigurationItem.getConfidentialityRating()));
	}

	@Transactional
	public void deleteConfigurationItem(String configurationItem) {
		log.debug("Delete configuration item entity with id {}",configurationItem);
		var entity = repository.findConfigurationItemEntityById(configurationItem);
		if(entity == null) {
			throw new NonExistentConfigurationItemException("Configuration item " + configurationItem + " not found");
		}

		repository.deleteById(configurationItem);
		log.debug("Deleted configuration item entity with id {}",configurationItem);
		ciCache.evictIfPresent(getCacheKey(entity.getAvailabilityRating(), entity.getIntegrityRating(), entity.getConfidentialityRating()));
		log.debug("Evicted cache record for min aic {}{}{}", entity.getAvailabilityRating(), entity.getIntegrityRating(), entity.getConfidentialityRating());
	}

	private String getCacheKey(Integer minAvailability, Integer minIntegrity, Integer minConfidentiality) {
		return "ci_" + minAvailability + minIntegrity + minConfidentiality;
	}
}
