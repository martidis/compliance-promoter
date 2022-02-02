package com.tasosmartidis.compliancebar.features.core.service;

import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceShotProgress;
import com.tasosmartidis.compliancebar.features.core.model.domain.ConfigurationItem;
import com.tasosmartidis.compliancebar.features.core.model.domain.ConfigurationItemDetails;
import com.tasosmartidis.compliancebar.features.core.model.domain.Status;
import com.tasosmartidis.compliancebar.features.core.model.entity.ComplianceShotEntity;
import com.tasosmartidis.compliancebar.features.core.model.entity.ConfigurationItemEntity;
import com.tasosmartidis.compliancebar.features.core.model.exception.ResourceNotFoundException;
import com.tasosmartidis.compliancebar.features.core.repository.ConfigurationItemsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class ConfigurationItemsService {

	private final ConfigurationItemsRepository configurationItemsRepository;
	private final Cache complianceBarCache;
	public static final String ALL_CIS_CACHE_KEY = "all-cis";
	public static final String TEAMS_NUM_CACHE_KEY = "all-owners";

	final BiFunction<ComplianceShotEntity, Status, ComplianceShotProgress> entityToComplianceShotProgress = (e, s) ->
		ComplianceShotProgress.builder()
				.complianceShot(e.entityToComplianceShot())
				.status(s)
				.build();

	public Set<ConfigurationItem> retrieveConfigurationItems() {
		var cachedResponse = complianceBarCache.get(ALL_CIS_CACHE_KEY, Set.class);
		if(cachedResponse != null) {
			return cachedResponse;
		}

		log.info("Retrieving configuration items from db");

		var configurationItemEntities = configurationItemsRepository.findAll();
		var items = configurationItemEntities.stream().map(ConfigurationItemEntity::entityToConfigurationItem).collect(Collectors.toUnmodifiableSet());
		complianceBarCache.putIfAbsent(ALL_CIS_CACHE_KEY, items);
		return items;
	}

	public ConfigurationItemDetails retrieveConfigurationItemDetails(String id) {
		var configurationItemEntity = configurationItemsRepository.findConfigurationItemEntityById(id);
		if(configurationItemEntity == null) {
			throw new ResourceNotFoundException("Configuration item with id: '" + id + "' requested but not found!");
		}

		var complianceShots = configurationItemEntity.getComplianceShotsConfigurationItems().stream()
				.map(csci -> entityToComplianceShotProgress.apply(csci.getComplianceShot(), csci.getStatus()))
				.collect(Collectors.toUnmodifiableSet());

		return ConfigurationItemDetails.builder()
				.complianceShots(complianceShots)
				.configurationItem(configurationItemEntity.entityToConfigurationItem())
				.build();
	}

	public long getNumberOfTeams() {
		var cachedResponse = complianceBarCache.get(TEAMS_NUM_CACHE_KEY, Long.class);
		if(cachedResponse != null) {
			return cachedResponse;
		}

		log.info("Retrieving number of teams from db");

		Long numberOfTeams = Long.valueOf(configurationItemsRepository.getUniqueSystemOwners().size());
		complianceBarCache.putIfAbsent(TEAMS_NUM_CACHE_KEY, numberOfTeams);
		return numberOfTeams;
	}



}
