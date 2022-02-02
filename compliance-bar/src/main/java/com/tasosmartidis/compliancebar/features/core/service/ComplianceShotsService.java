package com.tasosmartidis.compliancebar.features.core.service;

import com.tasosmartidis.compliancebar.features.core.model.api.ComplianceShotRequest;
import com.tasosmartidis.compliancebar.features.core.model.api.ConfigurationItemsSelectionForShotRequest;
import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceShot;
import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceShotDetails;
import com.tasosmartidis.compliancebar.features.core.model.domain.ConfigurationItemProgress;
import com.tasosmartidis.compliancebar.features.core.model.domain.Status;
import com.tasosmartidis.compliancebar.features.core.model.entity.ComplianceShotEntity;
import com.tasosmartidis.compliancebar.features.core.model.entity.ConfigurationItemEntity;
import com.tasosmartidis.compliancebar.features.core.model.exception.ComplianceShotCreationException;
import com.tasosmartidis.compliancebar.features.core.model.exception.ResourceNotFoundException;
import com.tasosmartidis.compliancebar.features.core.repository.ComplianceShotsRepository;
import com.tasosmartidis.compliancebar.features.core.service.integrations.mycmdb.MyCMDBIntegrationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.tasosmartidis.compliancebar.features.core.service.ComplianceShotsConfigurationItemsService.OVERALL_PROGRESS_CACHE_KEY;
import static com.tasosmartidis.compliancebar.features.core.service.ConfigurationItemsService.ALL_CIS_CACHE_KEY;
import static com.tasosmartidis.compliancebar.features.core.service.ConfigurationItemsService.TEAMS_NUM_CACHE_KEY;

@Slf4j
@AllArgsConstructor
@Service
public class ComplianceShotsService {

	private final ComplianceShotsRepository complianceShotsRepository;
	private final ComplianceShotsConfigurationItemsService complianceShotsConfigurationItemsService;
	private final MyCMDBIntegrationService myCMDBIntegrationService;
	private final Cache complianceBarCache;

	public static final String ALL_SHOTS_CACHE_KEY = "all-shots";

	final BiFunction<ConfigurationItemEntity, Status, ConfigurationItemProgress> entityToConfigurationItemProgress = (e,s) ->
		ConfigurationItemProgress.builder()
			.configurationItem(e.entityToConfigurationItem())
			.status(s)
			.build();

	public Set<ComplianceShot> retrieveComplianceShots() {
		var cachedResponse = complianceBarCache.get(ALL_SHOTS_CACHE_KEY, Set.class);
		if(cachedResponse != null) {
			return cachedResponse;
		}

		log.info("Retrieving compliance shots from db");

		var complianceShotEntities = complianceShotsRepository.findAll();
		var shots = complianceShotEntities.stream().map(ComplianceShotEntity::entityToComplianceShot).collect(Collectors.toUnmodifiableSet());
		complianceBarCache.putIfAbsent(ALL_SHOTS_CACHE_KEY, shots);
		return shots;
	}

	public ComplianceShotDetails retrieveComplianceShotDetails(String id) {
		var entity = complianceShotsRepository.findComplianceShotEntityById(id);
		if(entity == null) {
			throw new ResourceNotFoundException("Compliance shot with id: '" + id + "' requested but not found!");
		}

		var configurationItems = entity.getComplianceShotsConfigurationItems().stream().map(m -> entityToConfigurationItemProgress.apply(m.getConfigurationItem(), m.getStatus())).collect(Collectors.toUnmodifiableSet());

		return ComplianceShotDetails.builder()
				.applicableConfigurationItems(configurationItems)
				.complianceShot(entity.entityToComplianceShot())
				.build();
	}

	public ConfigurationItemsSelectionForShotRequest createCIsSelectionForShotRequest(ComplianceShot complianceShot, Authentication authentication) {
		UserDetails user = (UserDetails) authentication.getPrincipal();
		complianceShot.setCreatedBy(user.getUsername());

		var applicableConfigurationItems =
				myCMDBIntegrationService.getConfigurationItemsWithMinAIC(
												complianceShot.getMinAvailabilityRating(),
												complianceShot.getMinIntegrityRating(),
												complianceShot.getMinConfidentialityRating());

		var complianceShotRequest = new ComplianceShotRequest();
		complianceShotRequest.setComplianceShot(complianceShot);
		complianceShotRequest.setApplicableConfigurationItems(applicableConfigurationItems);

		var id = UUID.randomUUID().toString();
		complianceBarCache.put(id,complianceShotRequest);

		return new ConfigurationItemsSelectionForShotRequest(new ArrayList<>(applicableConfigurationItems), id);
	}

	public void createComplianceShot(String complianceShotRequestId, Set<String> selectedCisInRequest) {
		var initialComplianceShotRequest = complianceBarCache.get(complianceShotRequestId, ComplianceShotRequest.class);
		if(initialComplianceShotRequest == null) {
			throw new ComplianceShotCreationException("Could not retrieve compliance shot with id: '" +
					complianceShotRequestId + "' from session storage");
		}

		var selectedCIs = initialComplianceShotRequest.getApplicableConfigurationItems().stream()
				.filter(ci -> selectedCisInRequest.contains(ci.getId()))
				.collect(Collectors.toUnmodifiableSet());
		initialComplianceShotRequest.setApplicableConfigurationItems(selectedCIs);

		complianceShotsConfigurationItemsService.createComplianceShot(initialComplianceShotRequest);

		// creating a compliance shot it potentially makes all our "generic" cached values outdated, so we evict them
		complianceBarCache.evictIfPresent(ALL_SHOTS_CACHE_KEY);
		complianceBarCache.evictIfPresent(TEAMS_NUM_CACHE_KEY);
		complianceBarCache.evictIfPresent(ALL_CIS_CACHE_KEY);
		complianceBarCache.evictIfPresent(OVERALL_PROGRESS_CACHE_KEY);
	}



	public boolean complianceShotTitleExists(String title) {
		return complianceShotsRepository.countComplianceShotEntityByTitle(title) > 0;
	}

}
