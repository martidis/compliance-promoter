package com.tasosmartidis.compliancebar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.tasosmartidis.compliancebar.features.core.model.api.ComplianceShotRequest;
import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceLevel;
import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceShot;
import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceShotDetails;
import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceShotProgress;
import com.tasosmartidis.compliancebar.features.core.model.domain.ConfigurationItem;
import com.tasosmartidis.compliancebar.features.core.model.domain.ConfigurationItemProgress;
import com.tasosmartidis.compliancebar.features.core.model.domain.Status;
import com.tasosmartidis.compliancebar.features.core.model.entity.ComplianceShotConfigurationItemEntity;
import com.tasosmartidis.compliancebar.features.core.model.entity.ComplianceShotConfigurationItemKey;
import com.tasosmartidis.compliancebar.features.core.model.entity.ComplianceShotEntity;
import com.tasosmartidis.compliancebar.features.core.model.entity.ConfigurationItemEntity;
import com.tasosmartidis.compliancebar.features.core.model.view.ComplianceShotDetailsView;
import com.tasosmartidis.compliancebar.features.core.model.view.ComplianceShotsView;
import com.tasosmartidis.compliancebar.features.core.model.view.ConfigurationItemDetailsView;
import com.tasosmartidis.compliancebar.features.core.model.view.ConfigurationItemsView;
import com.tasosmartidis.compliancebar.features.core.model.view.DashboardView;
import com.tasosmartidis.compliancebar.features.core.model.view.ProgressDetails;
import com.tasosmartidis.compliancebar.features.core.service.integrations.mycmdb.MyCMDBConfigurationItemResponse;

import java.util.Set;


public class TestUtils {

	private static final ObjectWriter OBJECT_WRITTER;

	public static final String CID = "8PhJL";
	public static final String CI_NAME = "cool-service";
	public static final String SYSTEM_OWNER = "cool-owner";
	public static final String TEAM_EMAIL = "cool.email@gmail.com";
	public static final String EXTERNAL_ID = "ee60b900-c176-4e6f-8aa7-14d2ddb3860b";
	public static final String EXTERNAL_CMDB_NAME = "cool-cmdb";

	public static final String SHOT_ID = "ee60b900-c176-4e6f-8aa7-14d2ddb3860b";
	public static final String SHOT_TITLE = "cool-service";
	public static final String SHOT_DESC = "cool-owner";
	public static final int MIN_AVAILABILITY = 3;
	public static final int MIN_INTEGRITY = 3;
	public static final int MIN_CONFIDENTIALITY = 3;
	public static final String TUT_URL = "http://www.example.com/tut";
	public static final String REF_URL = "http://www.example.com/ref";
	public static final String CREATED_BY = "ME";

	static {
		OBJECT_WRITTER = new ObjectMapper().writer().withDefaultPrettyPrinter();
	}


	public static ComplianceShot createComplianceShotStub(String id, int availabilityRating, int integrityRating, int confidentialityRating) {
		return ComplianceShot.builder()
				.id(id)
				.title("stub title")
				.shortDescription("stub description")
				.tutorialUrl("http://www.example.com/tutorial")
				.referenceUrl("http://www.example.com/reference")
				.minAvailabilityRating(availabilityRating)
				.minIntegrityRating(integrityRating)
				.minConfidentialityRating(confidentialityRating)
				.complianceLevel(ComplianceLevel.BLOCKING)
				.createdBy("stub creator")
				.build();
	}

	public static ComplianceShotProgress createComplianceShotProgressStub(Status status) {
		return ComplianceShotProgress.builder()
				.complianceShot(createComplianceShotStub("68515974-9427-4821-9d11-d62021f3f621", 3,3,3))
				.status(status)
				.build();
	}

	public static ConfigurationItem createConfigurationItemStub(String id) {
		var configurationItem = new ConfigurationItem();
		configurationItem.setId(id);
		configurationItem.setConfigurationItemName("stub name");
		configurationItem.setSystemOwner("stub owner");
		configurationItem.setTeamEmail("stub@email.com");
		configurationItem.setExternalCMDBName("stub external cmdb");
		configurationItem.setExternalCMDBId("cc0e7fc3-4846-4f78-9840-2b9fc1d63843");
		return configurationItem;
	}

	public static ConfigurationItemProgress createConfigurationItemProgressStub(Status status) {
		return ConfigurationItemProgress.builder()
				.configurationItem(createConfigurationItemStub("9e0c06c0-8014-4971-9c32-054a485e54aa"))
				.status(status)
				.build();
	}

	public static ConfigurationItemEntity createConfigurationItemEntityStub(String id, String name, String externalId,
					String externalCMDBName, String systemOwner, String teamEmail) {
		var entity = new ConfigurationItemEntity();
		entity.setId(id);
		entity.setName(name);
		entity.setExternalId(externalId);
		entity.setExternalCMDBName(externalCMDBName);
		entity.setSystemOwner(systemOwner);
		entity.setTeamEmail(teamEmail);
		return entity;
	}

	public static ComplianceShotEntity createComplianceShotEntityStub(String id, ComplianceLevel level, String title,
																	  String description, int minA, int minI, int minC,
																	  String tutorialUrl, String referenceUrl, String createdBy) {
		var entity = new ComplianceShotEntity();
		entity.setId(id);
		entity.setComplianceLevel(level);
		entity.setTitle(title);
		entity.setShortDescription(description);
		entity.setMinAvailabilityRating(minA);
		entity.setMinIntegrityRating(minI);
		entity.setMinConfidentialityRating(minC);
		entity.setTutorialUrl(tutorialUrl);
		entity.setReferenceUrl(referenceUrl);
		entity.setCreatedBy(createdBy);
		return entity;
	}

	public static ComplianceShotConfigurationItemEntity createCsCIStub(ComplianceShotEntity csEntity, ConfigurationItemEntity ciEntity, Status status) {
		var csci = new ComplianceShotConfigurationItemEntity();
		csci.setComplianceShot(csEntity);
		csci.setConfigurationItem(ciEntity);
		csci.setStatus(status);
		csci.setId(new ComplianceShotConfigurationItemKey(ciEntity.getId(), csEntity.getId()));
		return csci;
	}

	public static ComplianceShot createComplianceShotStub(String id, String title, String description, String tutorialUrl,
														  String referenceUrl, int minA, int minI, int minC, ComplianceLevel complianceLevel) {
		 return ComplianceShot.builder()
				 .id(id)
				 .title(title)
				 .shortDescription(description)
				 .tutorialUrl(tutorialUrl)
				 .referenceUrl(referenceUrl)
				 .minAvailabilityRating(minA)
				 .minIntegrityRating(minI)
				 .minConfidentialityRating(minC)
				 .complianceLevel(complianceLevel)
				 .createdBy("me")
				 .build();
	}

	public static ConfigurationItem createConfigurationItemStub(String id, String name, String externalId,
															   String externalCMDBName, String systemOwner, String teamEmail) {
		var configurationItem = new ConfigurationItem();
		configurationItem.setId(id);
		configurationItem.setConfigurationItemName(name);
		configurationItem.setExternalCMDBId(externalId);
		configurationItem.setExternalCMDBName(externalCMDBName);
		configurationItem.setSystemOwner(systemOwner);
		configurationItem.setTeamEmail(teamEmail);
		return configurationItem;
	}

	public static ComplianceShotRequest createComplianceShotRequestStub(Set<ConfigurationItem> configurationItems,
																		ComplianceShot complianceShot) {
		var complianceShotRequest = new ComplianceShotRequest();
		complianceShotRequest.setComplianceShot(complianceShot);
		complianceShotRequest.setApplicableConfigurationItems(configurationItems);
		return complianceShotRequest;
	}

	public static MyCMDBConfigurationItemResponse createMyCMDBResponse(String id, String name, String systemOwner, String teamEmail) {
		var response = new MyCMDBConfigurationItemResponse();
		response.setId(id);
		response.setConfigurationItemName(name);
		response.setSystemOwner(systemOwner);
		response.setTeamEmail(teamEmail);
		return response;
	}

	public static DashboardView createDashboardViewStub() {
		var dashboardView = new DashboardView();
		var complianceShot = createComplianceShotStub(SHOT_ID,  SHOT_TITLE, SHOT_DESC, TUT_URL, REF_URL, MIN_AVAILABILITY,
				MIN_INTEGRITY, MIN_CONFIDENTIALITY,  ComplianceLevel.BLOCKING);
		var configurationItem = createConfigurationItemStub(CID, CI_NAME, EXTERNAL_ID, EXTERNAL_CMDB_NAME, SYSTEM_OWNER, TEAM_EMAIL);
		dashboardView.setComplianceShots(Set.of(complianceShot));
		dashboardView.setConfigurationItems(Set.of(configurationItem));
		dashboardView.setNumberOfComplianceShots(1);
		dashboardView.setNumberOfConfigurationItems(1);
		dashboardView.setNumberOfTeams(1);
		dashboardView.setProgressDetails(ProgressDetails.builder().inProgress(1).build());
		return dashboardView;
	}

	public static ComplianceShotsView createComplianceShotsView() {
		var complianceShot = createComplianceShotStub(SHOT_ID,  SHOT_TITLE, SHOT_DESC, TUT_URL, REF_URL, MIN_AVAILABILITY,
													MIN_INTEGRITY, MIN_CONFIDENTIALITY,  ComplianceLevel.BLOCKING);
		return new ComplianceShotsView(Set.of(complianceShot));
	}

	public static ConfigurationItemsView createConfigurationItemsView() {
		var configurationItem = createConfigurationItemStub(CID, CI_NAME, EXTERNAL_ID, EXTERNAL_CMDB_NAME, SYSTEM_OWNER, TEAM_EMAIL);
		return new ConfigurationItemsView(Set.of(configurationItem));
	}

	public static ConfigurationItemDetailsView createConfigurationItemDetailsView() {
		var complianceShotProgress = createComplianceShotProgressStub(Status.COMPLETED);
		var configurationItem = createConfigurationItemStub(CID, CI_NAME, EXTERNAL_ID, EXTERNAL_CMDB_NAME, SYSTEM_OWNER, TEAM_EMAIL);

		return new ConfigurationItemDetailsView(
									Set.of(complianceShotProgress),
									configurationItem,
									ProgressDetails.builder().cancelled(1).build());
	}

	public static ComplianceShotDetailsView createComplianceShotDetailsView() {
		var complianceShot = createComplianceShotStub(SHOT_ID,  SHOT_TITLE, SHOT_DESC, TUT_URL, REF_URL, MIN_AVAILABILITY,
				MIN_INTEGRITY, MIN_CONFIDENTIALITY,  ComplianceLevel.BLOCKING);
		return new ComplianceShotDetailsView(createComplianceShotDetailsStub(),
				ProgressDetails.builder().inProgress(1).build());
	}

	private static ComplianceShotDetails createComplianceShotDetailsStub() {
		var complianceShot = createComplianceShotStub(SHOT_ID,  SHOT_TITLE, SHOT_DESC, TUT_URL, REF_URL, MIN_AVAILABILITY,
				MIN_INTEGRITY, MIN_CONFIDENTIALITY,  ComplianceLevel.BLOCKING);
		var ciProgress = createConfigurationItemProgressStub(Status.CREATED);

		return ComplianceShotDetails.builder()
				.complianceShot(complianceShot)
				.applicableConfigurationItems(Set.of(ciProgress))
				.build();
	}

}
