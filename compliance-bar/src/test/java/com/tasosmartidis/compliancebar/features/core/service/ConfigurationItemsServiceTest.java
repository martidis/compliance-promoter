package com.tasosmartidis.compliancebar.features.core.service;

import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceLevel;
import com.tasosmartidis.compliancebar.features.core.model.domain.Status;
import com.tasosmartidis.compliancebar.features.core.model.exception.ResourceNotFoundException;
import com.tasosmartidis.compliancebar.features.core.repository.ConfigurationItemsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.cache.Cache;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
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
import static com.tasosmartidis.compliancebar.TestUtils.createConfigurationItemEntityStub;
import static com.tasosmartidis.compliancebar.TestUtils.createCsCIStub;
import static com.tasosmartidis.compliancebar.features.core.service.ConfigurationItemsService.ALL_CIS_CACHE_KEY;
import static com.tasosmartidis.compliancebar.features.core.service.ConfigurationItemsService.TEAMS_NUM_CACHE_KEY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ConfigurationItemsServiceTest {

	@InjectMocks
	ConfigurationItemsService configurationItemsService;

	@Mock
	ConfigurationItemsRepository configurationItemsRepositoryMock;
	@Mock
	Cache cacheMock;

	@Test
	void retrieveConfigurationItems_callsExpectedServicesOnce() {
		configurationItemsService.retrieveConfigurationItems();

		verify(configurationItemsRepositoryMock, times(1)).findAll();
		verify(cacheMock, times(1)).get(ALL_CIS_CACHE_KEY, Set.class);
		verify(cacheMock, times(1)).putIfAbsent(eq(ALL_CIS_CACHE_KEY), any(Set.class));
	}

	@Test
	void retrieveConfigurationItems_WhenResponseInCache_RepoNotCalled() {
		when(cacheMock.get(ALL_CIS_CACHE_KEY, Set.class)).thenReturn(new HashSet());

		configurationItemsService.retrieveConfigurationItems();

		verify(cacheMock, times(1)).get(ALL_CIS_CACHE_KEY, Set.class);
		verify(configurationItemsRepositoryMock, times(0)).findAll();
		verify(cacheMock, times(0)).putIfAbsent(eq(ALL_CIS_CACHE_KEY), any(Set.class));
	}

	@Test
	void retrieveConfigurationItemDetails_callsExpectedServiceOnce() {
		var ciEntityStub = createConfigurationItemEntityStub(CID, CI_NAME, EXTERNAL_ID, EXTERNAL_CMDB_NAME, SYSTEM_OWNER, TEAM_EMAIL);
		var csEntityStub = createComplianceShotEntityStub(SHOT_ID, ComplianceLevel.BLOCKING, SHOT_TITLE, SHOT_DESC, MIN_AVAILABILITY,
															MIN_INTEGRITY, MIN_CONFIDENTIALITY, TUT_URL, REF_URL, CREATED_BY);
		var csciStub = createCsCIStub(csEntityStub, ciEntityStub, Status.COMPLETED);
		when(configurationItemsRepositoryMock.findConfigurationItemEntityById(CID)).thenReturn(ciEntityStub);
		ciEntityStub.setComplianceShotsConfigurationItems(Set.of(csciStub));

		configurationItemsService.retrieveConfigurationItemDetails(CID);

		verify(configurationItemsRepositoryMock, times(1)).findConfigurationItemEntityById(CID);
	}

	@Test
	void retrieveConfigurationItemDetails_whenItemDoesntExistInDB_ThrowsException() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			configurationItemsService.retrieveConfigurationItemDetails(CID);
		});
	}

	@Test
	void getNumberOfTeams_callsExpectedServicesOnce() {
		when(configurationItemsRepositoryMock.getUniqueSystemOwners()).thenReturn(new ArrayList<>());

		configurationItemsService.getNumberOfTeams();

		verify(configurationItemsRepositoryMock, times(1)).getUniqueSystemOwners();
		verify(cacheMock, times(1)).get(TEAMS_NUM_CACHE_KEY, Long.class);
		verify(cacheMock, times(1)).putIfAbsent(TEAMS_NUM_CACHE_KEY, 0l);
	}

	@Test
	void getNumberOfTeams_WhenResponseInCache_ThenRepoNotCalled() {
		when(cacheMock.get(TEAMS_NUM_CACHE_KEY, Long.class)).thenReturn(1l);

		configurationItemsService.getNumberOfTeams();

		verify(configurationItemsRepositoryMock, times(0)).getUniqueSystemOwners();
		verify(cacheMock, times(1)).get(TEAMS_NUM_CACHE_KEY, Long.class);
		verify(cacheMock, times(0)).putIfAbsent(any(), any());
	}
}
