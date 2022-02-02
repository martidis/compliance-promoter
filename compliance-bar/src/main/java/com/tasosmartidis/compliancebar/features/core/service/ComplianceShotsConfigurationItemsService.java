package com.tasosmartidis.compliancebar.features.core.service;

import com.tasosmartidis.compliancebar.features.core.model.api.ComplianceShotRequest;
import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceShotConfigurationItemProgress;
import com.tasosmartidis.compliancebar.features.core.model.domain.ConfigurationItem;
import com.tasosmartidis.compliancebar.features.core.model.domain.Status;
import com.tasosmartidis.compliancebar.features.core.model.entity.ComplianceShotConfigurationItemEntity;
import com.tasosmartidis.compliancebar.features.core.model.entity.ComplianceShotConfigurationItemKey;
import com.tasosmartidis.compliancebar.features.core.model.entity.ComplianceShotEntity;
import com.tasosmartidis.compliancebar.features.core.model.entity.ConfigurationItemEntity;
import com.tasosmartidis.compliancebar.features.core.model.exception.ComplianceShotCreationException;
import com.tasosmartidis.compliancebar.features.core.repository.ComplianceShotConfigurationItemRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class ComplianceShotsConfigurationItemsService {
	private final ComplianceShotConfigurationItemRepository complianceShotConfigurationItemRepository;
	private final Cache complianceBarCache;

	public static final String OVERALL_PROGRESS_CACHE_KEY = "overall-progress";

	public Set<ComplianceShotConfigurationItemProgress> getOverallProgress() {
		var cachedResponse = complianceBarCache.get(OVERALL_PROGRESS_CACHE_KEY, Set.class);
		if(cachedResponse != null) {
			return cachedResponse;
		}

		log.info("Retrieving compliance shots/configuration items junction from db");

		var complianceShotEntities = complianceShotConfigurationItemRepository.findAll();
		var progress = complianceShotEntities.stream()
				.map(ComplianceShotConfigurationItemEntity::entityToComplianceShotConfigurationItemProgress)
				.collect(Collectors.toSet());
		complianceBarCache.putIfAbsent(OVERALL_PROGRESS_CACHE_KEY, progress);
		return progress;
	}

	public void createComplianceShot(ComplianceShotRequest request) {
		var complianceShotEntity = complianceShotToEntity(request);
		var configurationItemEntities = request.getApplicableConfigurationItems().stream()
				.map(this::configurationItemToEntity)
				.collect(Collectors.toSet());

		var complianceShotConfigurationItemEntities = configurationItemEntities.stream()
				.map(ci -> complianceShotConfigurationItemToEntity(complianceShotEntity, ci))
				.collect(Collectors.toSet());

		if(complianceShotConfigurationItemEntities.isEmpty()) {
			throw new ComplianceShotCreationException("At least one configuration item must be selected for the shot");
		}

		complianceShotConfigurationItemRepository.saveAll(complianceShotConfigurationItemEntities);
	}

	private ComplianceShotConfigurationItemEntity complianceShotConfigurationItemToEntity(ComplianceShotEntity complianceShotEntity, ConfigurationItemEntity ci) {
		var csci = new ComplianceShotConfigurationItemEntity();
		csci.setComplianceShot(complianceShotEntity);
		csci.setConfigurationItem(ci);
		csci.setStatus(Status.CREATED);
		csci.setId(new ComplianceShotConfigurationItemKey(ci.getId(), complianceShotEntity.getId()));
		return csci;
	}

	private ComplianceShotEntity complianceShotToEntity(ComplianceShotRequest request) {
		var entity = new ComplianceShotEntity();
		entity.setId(UUID.randomUUID().toString());
		entity.setComplianceLevel(request.getComplianceShot().getComplianceLevel());
		entity.setTitle(request.getComplianceShot().getTitle());
		entity.setShortDescription(request.getComplianceShot().getShortDescription());
		entity.setMinAvailabilityRating(request.getComplianceShot().getMinAvailabilityRating());
		entity.setMinIntegrityRating(request.getComplianceShot().getMinIntegrityRating());
		entity.setMinConfidentialityRating(request.getComplianceShot().getMinConfidentialityRating());
		entity.setReferenceUrl(request.getComplianceShot().getReferenceUrl());
		entity.setTutorialUrl(request.getComplianceShot().getTutorialUrl());
		entity.setCreatedBy(request.getComplianceShot().getCreatedBy());
		return entity;
	}

	private ConfigurationItemEntity configurationItemToEntity(ConfigurationItem ci) {
		var entity = new ConfigurationItemEntity();
		entity.setId(ci.getId());
		entity.setExternalId(ci.getExternalCMDBId());
		entity.setName(ci.getConfigurationItemName());
		entity.setSystemOwner(ci.getSystemOwner());
		entity.setTeamEmail(ci.getTeamEmail());
		entity.setExternalCMDBName(ci.getExternalCMDBName());
		return entity;
	}


}
