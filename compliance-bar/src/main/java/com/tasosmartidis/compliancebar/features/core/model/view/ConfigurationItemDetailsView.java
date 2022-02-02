package com.tasosmartidis.compliancebar.features.core.model.view;

import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceShotProgress;
import com.tasosmartidis.compliancebar.features.core.model.domain.ConfigurationItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public class ConfigurationItemDetailsView implements Viewable {
	private Set<ComplianceShotProgress> complianceShots;
	private ConfigurationItem configurationItem;
	private ProgressDetails progressDetails;
}
