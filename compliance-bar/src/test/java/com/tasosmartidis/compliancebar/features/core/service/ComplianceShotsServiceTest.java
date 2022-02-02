package com.tasosmartidis.compliancebar.features.core.service;

import com.tasosmartidis.compliancebar.features.core.model.api.ComplianceShotRequest;
import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceLevel;
import com.tasosmartidis.compliancebar.features.core.model.domain.Status;
import com.tasosmartidis.compliancebar.features.core.model.exception.ComplianceShotCreationException;
import com.tasosmartidis.compliancebar.features.core.model.exception.ResourceNotFoundException;
import com.tasosmartidis.compliancebar.features.core.repository.ComplianceShotsRepository;
import com.tasosmartidis.compliancebar.features.core.service.integrations.mycmdb.MyCMDBIntegrationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;

import static com.tasosmartidis.compliancebar.TestUtils.CID;
import static com.tasosmartidis.compliancebar.TestUtils.CI_NAME;
import static com.tasosmartidis.compliancebar.TestUtils.CREATED_BY;
import static com.tasosmartidis.compliancebar.TestUtils.EXTERNAL_CMDB_NAME;
import static com.tasosmartidis.compliancebar.TestUtils.EXTERNAL_ID;
import static com.tasosmartidis.compliancebar.TestUtils.MIN_AVAILABILITY;
import static com.tasosmartidis.compliancebar.TestUtils.MIN_CONFIDENTIALITY;
import static com.tasosmartidis.compliancebar.TestUtils.MIN_INTEGRITY;
import static com.tasosmartidis.compliancebar.TestUtils.REF_URL;
import static com.tasosmartidis.compliancebar.TestUtils.SHOT_DESC;
import static com.tasosmartidis.compliancebar.TestUtils.SHOT_ID;
import static com.tasosmartidis.compliancebar.TestUtils.SHOT_TITLE;
import static com.tasosmartidis.compliancebar.TestUtils.SYSTEM_OWNER;
import static com.tasosmartidis.compliancebar.TestUtils.TEAM_EMAIL;
import static com.tasosmartidis.compliancebar.TestUtils.TUT_URL;
import static com.tasosmartidis.compliancebar.TestUtils.createComplianceShotEntityStub;
import static com.tasosmartidis.compliancebar.TestUtils.createComplianceShotRequestStub;
import static com.tasosmartidis.compliancebar.TestUtils.createComplianceShotStub;
import static com.tasosmartidis.compliancebar.TestUtils.createConfigurationItemEntityStub;
import static com.tasosmartidis.compliancebar.TestUtils.createConfigurationItemStub;
import static com.tasosmartidis.compliancebar.TestUtils.createCsCIStub;
import static com.tasosmartidis.compliancebar.features.core.service.ComplianceShotsConfigurationItemsService.OVERALL_PROGRESS_CACHE_KEY;
import static com.tasosmartidis.compliancebar.features.core.service.ComplianceShotsService.ALL_SHOTS_CACHE_KEY;
import static com.tasosmartidis.compliancebar.features.core.service.ConfigurationItemsService.ALL_CIS_CACHE_KEY;
import static com.tasosmartidis.compliancebar.features.core.service.ConfigurationItemsService.TEAMS_NUM_CACHE_KEY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ComplianceShotsServiceTest {

	@InjectMocks
	ComplianceShotsService complianceShotsService;

	@Mock
	ComplianceShotsRepository repositoryMock;
	@Mock
	ComplianceShotsConfigurationItemsService complianceShotsConfigurationItemsServiceMock;
	@Mock
	MyCMDBIntegrationService myCMDBIntegrationServiceMock;
	@Mock
	@Qualifier("complianceBarCache")
	Cache complianceBarCacheMock;
	@Mock
	Authentication authenticationMock;
	@Mock
	User userMock;

	@Test
	void retrieveComplianceShots_callsExpectedServicesOnce() {
		complianceShotsService.retrieveComplianceShots();

		verify(repositoryMock, times(1)).findAll();
		verify(complianceBarCacheMock, times(1)).get(ALL_SHOTS_CACHE_KEY, Set.class);
		verify(complianceBarCacheMock, times(1)).putIfAbsent(eq(ALL_SHOTS_CACHE_KEY), anySet());
	}

	@Test
	void retrieveComplianceShots_WhenInCacheRepoNotCalled() {
		when(complianceBarCacheMock.get(ALL_SHOTS_CACHE_KEY, Set.class)).thenReturn(new HashSet<>());

		complianceShotsService.retrieveComplianceShots();

		verify(repositoryMock, times(0)).findAll();
		verify(complianceBarCacheMock, times(1)).get(ALL_SHOTS_CACHE_KEY, Set.class);
		verify(complianceBarCacheMock, times(0)).putIfAbsent(anyString(), anySet());
	}

	@Test
	void retrieveComplianceShotDetails_callsExpectedServiceOnce() {
		var ciEntityStub = createConfigurationItemEntityStub(CID, CI_NAME, EXTERNAL_ID, EXTERNAL_CMDB_NAME, SYSTEM_OWNER, TEAM_EMAIL);
		var csEntityStub = createComplianceShotEntityStub(SHOT_ID, ComplianceLevel.BLOCKING, SHOT_TITLE, SHOT_DESC, MIN_AVAILABILITY,
																	MIN_INTEGRITY, MIN_CONFIDENTIALITY, TUT_URL, REF_URL, CREATED_BY);
		var csciStub = createCsCIStub(csEntityStub, ciEntityStub, Status.COMPLETED);
		when(repositoryMock.findComplianceShotEntityById(SHOT_ID)).thenReturn(csEntityStub);
		csEntityStub.setComplianceShotsConfigurationItems(Set.of(csciStub));


		complianceShotsService.retrieveComplianceShotDetails(SHOT_ID);

		verify(repositoryMock, times(1)).findComplianceShotEntityById(SHOT_ID);

	}

	@Test
	void retrieveComplianceShotDetails_whenItemDoesntExistInDB_ThrowsException() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			complianceShotsService.retrieveComplianceShotDetails(SHOT_ID);
		});
	}

	@Test
	void createCIsSelectionForShotRequest_whenHappyFlow_correctItemsInCreatedRequest() {
		var complianceShot = createComplianceShotStub(SHOT_ID,  SHOT_TITLE, SHOT_DESC, TUT_URL, REF_URL, MIN_AVAILABILITY,
				MIN_INTEGRITY, MIN_CONFIDENTIALITY,  ComplianceLevel.BLOCKING);
		var configurationItem = createConfigurationItemStub(CID, CI_NAME, EXTERNAL_ID, EXTERNAL_CMDB_NAME, SYSTEM_OWNER, TEAM_EMAIL);
		when(authenticationMock.getPrincipal()).thenReturn(userMock);
		when(myCMDBIntegrationServiceMock.getConfigurationItemsWithMinAIC(MIN_AVAILABILITY, MIN_INTEGRITY, MIN_CONFIDENTIALITY))
				.thenReturn(Set.of(configurationItem));

		var actual = complianceShotsService.createCIsSelectionForShotRequest(complianceShot, authenticationMock);

		// this is not nice
		assertEquals(1, actual.getApplicableConfigurationItems().size());
		assertEquals(configurationItem, actual.getApplicableConfigurationItems().get(0));
	}


	@Test
	void createCIsSelectionForShotRequest_callsExpectedServicesOnce() {
		var complianceShot = createComplianceShotStub(SHOT_ID,  SHOT_TITLE, SHOT_DESC, TUT_URL, REF_URL, MIN_AVAILABILITY,
				MIN_INTEGRITY, MIN_CONFIDENTIALITY,  ComplianceLevel.BLOCKING);
		var configurationItem = createConfigurationItemStub(CID, CI_NAME, EXTERNAL_ID, EXTERNAL_CMDB_NAME, SYSTEM_OWNER, TEAM_EMAIL);
		when(authenticationMock.getPrincipal()).thenReturn(userMock);
		when(myCMDBIntegrationServiceMock.getConfigurationItemsWithMinAIC(MIN_AVAILABILITY, MIN_INTEGRITY, MIN_CONFIDENTIALITY))
				.thenReturn(Set.of(configurationItem));

		complianceShotsService.createCIsSelectionForShotRequest(complianceShot, authenticationMock);

		verify(myCMDBIntegrationServiceMock, times(1)).getConfigurationItemsWithMinAIC(MIN_AVAILABILITY, MIN_INTEGRITY, MIN_CONFIDENTIALITY);
		verify(complianceBarCacheMock, times(1)).put(anyString(), isA(ComplianceShotRequest.class));
	}

	@Test
	void createComplianceShot_createsShotOnlyWithSelectedCIs() {
		var requestId = "6fc1de9b-b4c0-409e-bd34-7d4226ffc399";
		var selectedCIs = Set.of(CID);
		var configurationItem = createConfigurationItemStub(CID, CI_NAME, EXTERNAL_ID, EXTERNAL_CMDB_NAME, SYSTEM_OWNER, TEAM_EMAIL);
		var aNonSelectedCI = createConfigurationItemStub("non-selected-id", CI_NAME, EXTERNAL_ID, EXTERNAL_CMDB_NAME, SYSTEM_OWNER, TEAM_EMAIL);
		var complianceShot = createComplianceShotStub(SHOT_ID,  SHOT_TITLE, SHOT_DESC, TUT_URL, REF_URL, MIN_AVAILABILITY,
				MIN_INTEGRITY, MIN_CONFIDENTIALITY,  ComplianceLevel.BLOCKING);
		var complianceShotRequest = createComplianceShotRequestStub(Set.of(configurationItem, aNonSelectedCI), complianceShot);
		when(complianceBarCacheMock.get(requestId, ComplianceShotRequest.class)).thenReturn(complianceShotRequest);

		complianceShotsService.createComplianceShot(requestId,selectedCIs);

		verify(complianceShotsConfigurationItemsServiceMock, times(1)).createComplianceShot(argThat(selectedCIsArgumentMatcher()));

	}

	private ArgumentMatcher<ComplianceShotRequest> selectedCIsArgumentMatcher() {
		return r -> {
			assertThat(r.getApplicableConfigurationItems().size()).isEqualTo(1);
			return true;
		};
	}

	@Test
	void createComplianceShot_whenRequestIdDoesntFindRequestInCache_throwsException() {
		var requestId = "1f90dd8d-3294-4f91-b14a-50c170762a2e";
		var selectedCIs = Set.of(SHOT_ID);

		Assertions.assertThrows(ComplianceShotCreationException.class, () -> {
			complianceShotsService.createComplianceShot(requestId,selectedCIs);
		});
	}

	@Test
	void createComplianceShot_callsExpectedServiceOnce() {
		var requestId = "6fc1de9b-b4c0-409e-bd34-7d4226ffc399";
		var selectedCIs = Set.of(SHOT_ID);
		var configurationItem = createConfigurationItemStub(CID, CI_NAME, EXTERNAL_ID, EXTERNAL_CMDB_NAME, SYSTEM_OWNER, TEAM_EMAIL);
		var complianceShot = createComplianceShotStub(SHOT_ID,  SHOT_TITLE, SHOT_DESC, TUT_URL, REF_URL, MIN_AVAILABILITY,
				MIN_INTEGRITY, MIN_CONFIDENTIALITY,  ComplianceLevel.BLOCKING);
		var complianceShotRequest = createComplianceShotRequestStub(Set.of(configurationItem), complianceShot);
		when(complianceBarCacheMock.get(requestId, ComplianceShotRequest.class)).thenReturn(complianceShotRequest);

		complianceShotsService.createComplianceShot(requestId,selectedCIs);

		verify(complianceShotsConfigurationItemsServiceMock, times(1)).createComplianceShot(complianceShotRequest);
		verify(complianceBarCacheMock, times(1)).evictIfPresent(ALL_SHOTS_CACHE_KEY);
		verify(complianceBarCacheMock, times(1)).evictIfPresent(TEAMS_NUM_CACHE_KEY);
		verify(complianceBarCacheMock, times(1)).evictIfPresent(ALL_CIS_CACHE_KEY);
		verify(complianceBarCacheMock, times(1)).evictIfPresent(OVERALL_PROGRESS_CACHE_KEY);
	}



	@Test
	void complianceShotTitleExists_callsExpectedServiceOnce() {
		var id = "99fc25a6-64e4-4c39-85fc-ad5a15a1f4e8";

		complianceShotsService.complianceShotTitleExists(id);

		verify(repositoryMock, times(1)).countComplianceShotEntityByTitle(id);
	}

	@Test
	void complianceShotTitleExists_WhenExistsReturnsTrue() {
		var id = "99fc25a6-64e4-4c39-85fc-ad5a15a1f4e8";
		when(repositoryMock.countComplianceShotEntityByTitle(id)).thenReturn(1l);

		assertTrue(complianceShotsService.complianceShotTitleExists(id));
	}

	@Test
	void complianceShotTitleExists_WhenNotExistsReturnsFalse() {
		var id = "99fc25a6-64e4-4c39-85fc-ad5a15a1f4e8";
		when(repositoryMock.countComplianceShotEntityByTitle(id)).thenReturn(0l);

		assertFalse(complianceShotsService.complianceShotTitleExists(id));
	}
}
