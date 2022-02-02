package com.tasosmartidis.compliancebar.features.core.service;

import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceShotDetails;
import com.tasosmartidis.compliancebar.features.core.model.domain.ConfigurationItemDetails;
import com.tasosmartidis.compliancebar.features.core.model.domain.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static com.tasosmartidis.compliancebar.TestUtils.createComplianceShotProgressStub;
import static com.tasosmartidis.compliancebar.TestUtils.createComplianceShotStub;
import static com.tasosmartidis.compliancebar.TestUtils.createConfigurationItemProgressStub;
import static com.tasosmartidis.compliancebar.TestUtils.createConfigurationItemStub;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ViewsConstructionServiceTest {

	@InjectMocks
	ViewsConstructionService viewsConstructionService;

	@Mock
	ComplianceShotsService complianceShotsServiceMock;
	@Mock
	ConfigurationItemsService configurationItemsServiceMock;
	@Mock
	ComplianceShotsConfigurationItemsService complianceShotsConfigurationItemsServiceMock;

	@Mock
	ComplianceShotDetails complianceShotDetailsMock;
	@Mock
	ConfigurationItemDetails configurationItemDetailsMock;

	@Test
	void createComplianceShotsOverview_callsExpectedServiceOnce() {
		viewsConstructionService.createComplianceShotsOverview();

		verify(complianceShotsServiceMock, times(1)).retrieveComplianceShots();
	}

	@Test
	void createComplianceShotsOverview_transformationIsCorrect() {
		var shotStub = createComplianceShotStub("13f3446f-b69f-42ad-8279-15629a6f6f3e", 3,3,3);
		var expectedShots = Set.of(shotStub);
		when(complianceShotsServiceMock.retrieveComplianceShots()).thenReturn(expectedShots);

		var actualView = viewsConstructionService.createComplianceShotsOverview();

		assertEquals(expectedShots, actualView.getComplianceShots());
	}

	@Test
	void createComplianceShotDetailsView_callsExpectedServiceOnce() {
		var id = "eb6be348-8848-4bb4-9167-3daea58d1aa9";
		var completed = createConfigurationItemProgressStub(Status.COMPLETED);
		when(complianceShotDetailsMock.getApplicableConfigurationItems()).thenReturn(Set.of(completed));
		when(complianceShotsServiceMock.retrieveComplianceShotDetails(id)).thenReturn(complianceShotDetailsMock);

		viewsConstructionService.createComplianceShotDetailsView(id);

		verify(complianceShotsServiceMock, times(1)).retrieveComplianceShotDetails(id);
	}

	@Test
	void createComplianceShotDetailsView_calculatesProgressDetailsCorrectly() {
		var id = "eb6be348-8848-4bb4-9167-3daea58d1aa9";
		var completed = createConfigurationItemProgressStub(Status.COMPLETED);
		var created = createConfigurationItemProgressStub(Status.CREATED);
		var submitted = createConfigurationItemProgressStub(Status.SUBMITTED);
		var cancelled = createConfigurationItemProgressStub(Status.CANCELLED);
		when(complianceShotDetailsMock.getApplicableConfigurationItems()).thenReturn(Set.of(completed, created, submitted, cancelled));
		when(complianceShotsServiceMock.retrieveComplianceShotDetails(id)).thenReturn(complianceShotDetailsMock);

		var actualView = viewsConstructionService.createComplianceShotDetailsView(id);

		assertEquals(2, actualView.getProgressDetails().getInProgress()); // created + submitted
		assertEquals(1, actualView.getProgressDetails().getCompleted());
		assertEquals(1, actualView.getProgressDetails().getCancelled());
	}

	@Test
	void createConfigurationItemsView_callsExpectedServiceOnce() {
		viewsConstructionService.createConfigurationItemsView();

		verify(configurationItemsServiceMock, times(1)).retrieveConfigurationItems();
	}

	@Test
	void createConfigurationItemsView_transformationIsCorrect() {
		var ciStub = createConfigurationItemStub("13f3446f-b69f-42ad-8279-15629a6f6f3e");
		var expectedCIs = Set.of(ciStub);
		when(configurationItemsServiceMock.retrieveConfigurationItems()).thenReturn(expectedCIs);

		var actualView = viewsConstructionService.createConfigurationItemsView();

		assertEquals(expectedCIs, actualView.getConfigurationItems());
	}

	@Test
	void createConfigurationItemDetailsView_callsExpectedServiceOnce() {
		var id = "58e41c5a-6a4a-4ac2-abf2-5a5b478c513e";
		var completed = createComplianceShotProgressStub(Status.COMPLETED);
		when(configurationItemDetailsMock.getComplianceShots()).thenReturn(Set.of(completed));
		when(configurationItemsServiceMock.retrieveConfigurationItemDetails(id)).thenReturn(configurationItemDetailsMock);

		viewsConstructionService.createConfigurationItemDetailsView(id);

		verify(configurationItemsServiceMock, times(1)).retrieveConfigurationItemDetails(id);

	}

	@Test
	void createConfigurationItemDetailsView_calculatesProgressDetailsCorrectly() {
		var id = "58e41c5a-6a4a-4ac2-abf2-5a5b478c513e";
		var completed = createComplianceShotProgressStub(Status.COMPLETED);
		var created = createComplianceShotProgressStub(Status.CREATED);
		var submitted = createComplianceShotProgressStub(Status.SUBMITTED);
		var cancelled = createComplianceShotProgressStub(Status.CANCELLED);
		when(configurationItemDetailsMock.getComplianceShots()).thenReturn(Set.of(completed, created, submitted, cancelled));
		when(configurationItemsServiceMock.retrieveConfigurationItemDetails(id)).thenReturn(configurationItemDetailsMock);

		var actualView = viewsConstructionService.createConfigurationItemDetailsView(id);

		assertEquals(2, actualView.getProgressDetails().getInProgress()); // created + submitted
		assertEquals(1, actualView.getProgressDetails().getCompleted());
		assertEquals(1, actualView.getProgressDetails().getCancelled());
	}

	@Test
	void createDashboardView_callsExpectedServices() {
		viewsConstructionService.createDashboardView();

		verify(configurationItemsServiceMock, times(1)).retrieveConfigurationItems();
		verify(complianceShotsServiceMock, times(1)).retrieveComplianceShots();
		verify(configurationItemsServiceMock, times(1)).getNumberOfTeams();
		verify(complianceShotsConfigurationItemsServiceMock, times(1)).getOverallProgress();
	}
}
