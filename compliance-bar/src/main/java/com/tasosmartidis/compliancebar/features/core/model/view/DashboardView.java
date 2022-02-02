package com.tasosmartidis.compliancebar.features.core.model.view;

import com.tasosmartidis.compliancebar.features.core.model.domain.ConfigurationItem;
import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceShot;
import lombok.Data;

import java.util.Set;

@Data
public class DashboardView implements Viewable {
	private long numberOfComplianceShots;
	private long numberOfTeams;
	private long numberOfConfigurationItems;
	private ProgressDetails progressDetails;
	private Set<ComplianceShot> complianceShots;
	private Set<ConfigurationItem> configurationItems;
}
