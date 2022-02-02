package com.tasosmartidis.compliancebar.features.core.service;

import com.tasosmartidis.compliancebar.features.core.model.view.ProgressDetails;
import com.tasosmartidis.compliancebar.features.core.model.domain.Status;
import com.tasosmartidis.compliancebar.features.core.model.view.ComplianceShotDetailsView;
import com.tasosmartidis.compliancebar.features.core.model.view.ComplianceShotsView;
import com.tasosmartidis.compliancebar.features.core.model.view.ConfigurationItemDetailsView;
import com.tasosmartidis.compliancebar.features.core.model.view.ConfigurationItemsView;
import com.tasosmartidis.compliancebar.features.core.model.view.DashboardView;
import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceShotConfigurationItemProgress;
import com.tasosmartidis.compliancebar.features.core.model.domain.Progressible;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

@AllArgsConstructor
@Service
public class ViewsConstructionService {

	private final ComplianceShotsService complianceShotsService;
	private final ConfigurationItemsService configurationItemsService;
	private final ComplianceShotsConfigurationItemsService complianceShotsConfigurationItemsService;

	public ComplianceShotsView createComplianceShotsOverview() {
		var complianceShots = complianceShotsService.retrieveComplianceShots();
		return new ComplianceShotsView(complianceShots);
	}

	public ComplianceShotDetailsView createComplianceShotDetailsView(String id) {
		var complianceShotDetails = complianceShotsService.retrieveComplianceShotDetails(id);
		return new ComplianceShotDetailsView(complianceShotDetails,
				getProgressDetails(complianceShotDetails.getApplicableConfigurationItems().stream()));
	}

	public ConfigurationItemsView createConfigurationItemsView() {
		var configurationItems = configurationItemsService.retrieveConfigurationItems();
		return new ConfigurationItemsView(configurationItems);
	}

	public ConfigurationItemDetailsView createConfigurationItemDetailsView(String id) {
		var configurationItemDetails = configurationItemsService.retrieveConfigurationItemDetails(id);
		return new ConfigurationItemDetailsView(configurationItemDetails.getComplianceShots(),
				configurationItemDetails.getConfigurationItem(),
				getProgressDetails(configurationItemDetails.getComplianceShots().stream()));
	}

	public DashboardView createDashboardView() {
		var complianceShots = complianceShotsService.retrieveComplianceShots();
		var configurationItems = configurationItemsService.retrieveConfigurationItems();

		var overallProgress = complianceShotsConfigurationItemsService.getOverallProgress();
		var numberOfShots = overallProgress.stream().map(ComplianceShotConfigurationItemProgress::getComplianceShotId).distinct().count();
		var numberOfCis = overallProgress.stream().map(ComplianceShotConfigurationItemProgress::getConfigurationItemId).distinct().count();

		var dashboardView = new DashboardView();
		dashboardView.setNumberOfComplianceShots(numberOfShots);
		dashboardView.setNumberOfConfigurationItems(numberOfCis);
		dashboardView.setNumberOfTeams(configurationItemsService.getNumberOfTeams());
		dashboardView.setComplianceShots(complianceShots.stream().limit(5).collect(Collectors.toSet()));
		dashboardView.setConfigurationItems(configurationItems.stream().limit(5).collect(Collectors.toSet()));

		dashboardView.setProgressDetails(getProgressDetails(overallProgress.stream()));

		return dashboardView;
	}

	private ProgressDetails getProgressDetails(Stream<? extends Progressible> items) {
		var itemsByStatus = items.collect(groupingBy(Progressible::getStatus));
		var created = itemsByStatus.get(Status.CREATED) != null ? itemsByStatus.get(Status.CREATED).size() : 0;
		var submitted = itemsByStatus.get(Status.SUBMITTED) != null ? itemsByStatus.get(Status.SUBMITTED).size() : 0;
		var completed = itemsByStatus.get(Status.COMPLETED) != null ? itemsByStatus.get(Status.COMPLETED).size() : 0;
		var cancelled = itemsByStatus.get(Status.CANCELLED) != null ? itemsByStatus.get(Status.CANCELLED).size() : 0;

		return ProgressDetails.builder()
				.inProgress(created + submitted)
				.cancelled(cancelled)
				.completed(completed)
				.build();
	}
}
