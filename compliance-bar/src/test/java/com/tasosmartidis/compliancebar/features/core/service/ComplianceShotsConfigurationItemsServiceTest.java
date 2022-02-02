package com.tasosmartidis.compliancebar.features.core.service;

import com.tasosmartidis.compliancebar.features.core.model.api.ComplianceShotRequest;
import com.tasosmartidis.compliancebar.features.core.model.exception.ComplianceShotCreationException;
import com.tasosmartidis.compliancebar.features.core.repository.ComplianceShotConfigurationItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.cache.Cache;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;

import static com.tasosmartidis.compliancebar.TestUtils.CID;
import static com.tasosmartidis.compliancebar.TestUtils.MIN_AVAILABILITY;
import static com.tasosmartidis.compliancebar.TestUtils.MIN_CONFIDENTIALITY;
import static com.tasosmartidis.compliancebar.TestUtils.MIN_INTEGRITY;
import static com.tasosmartidis.compliancebar.TestUtils.SHOT_ID;
import static com.tasosmartidis.compliancebar.TestUtils.createComplianceShotStub;
import static com.tasosmartidis.compliancebar.TestUtils.createConfigurationItemStub;
import static com.tasosmartidis.compliancebar.features.core.service.ComplianceShotsConfigurationItemsService.OVERALL_PROGRESS_CACHE_KEY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ComplianceShotsConfigurationItemsServiceTest {

	@InjectMocks
	ComplianceShotsConfigurationItemsService complianceShotsConfigurationItemsService;

	@Mock
	ComplianceShotConfigurationItemRepository complianceShotConfigurationItemRepositoryMock;
	@Mock
	Cache cacheMock;

	@Test
	void getOverallProgress_callsExpectedServicesOnce() {
		complianceShotsConfigurationItemsService.getOverallProgress();

		verify(complianceShotConfigurationItemRepositoryMock, times(1)).findAll();
		verify(cacheMock, times(1)).get(OVERALL_PROGRESS_CACHE_KEY, Set.class);
		verify(cacheMock, times(1)).putIfAbsent(eq(OVERALL_PROGRESS_CACHE_KEY), any(Set.class));
	}

	@Test
	void getOverallProgress_WhenInCacheRepoNotCalled() {
		when(cacheMock.get(OVERALL_PROGRESS_CACHE_KEY, Set.class)).thenReturn(new HashSet<>());

		complianceShotsConfigurationItemsService.getOverallProgress();

		verify(complianceShotConfigurationItemRepositoryMock, times(0)).findAll();
		verify(cacheMock, times(1)).get(OVERALL_PROGRESS_CACHE_KEY, Set.class);
		verify(cacheMock, times(0)).putIfAbsent(eq(OVERALL_PROGRESS_CACHE_KEY), any(Set.class));
	}

	@Test
	void createComplianceShot_callsExpectedServiceOnce() {
		var request = new ComplianceShotRequest();
		request.setComplianceShot(createComplianceShotStub(SHOT_ID, MIN_AVAILABILITY, MIN_INTEGRITY, MIN_CONFIDENTIALITY));
		request.setApplicableConfigurationItems(Set.of(createConfigurationItemStub(CID)));

		complianceShotsConfigurationItemsService.createComplianceShot(request);

		verify(complianceShotConfigurationItemRepositoryMock, times(1)).saveAll(anySet());
	}

	@Test
	void createComplianceShot_WhenNoConfigurationItemsSelected_throwsException() {
		var request = new ComplianceShotRequest();
		request.setComplianceShot(createComplianceShotStub(SHOT_ID, MIN_AVAILABILITY, MIN_INTEGRITY, MIN_CONFIDENTIALITY));
		request.setApplicableConfigurationItems(new HashSet<>());

		Assertions.assertThrows(ComplianceShotCreationException.class, () -> {
			complianceShotsConfigurationItemsService.createComplianceShot(request);
		});
	}
}
