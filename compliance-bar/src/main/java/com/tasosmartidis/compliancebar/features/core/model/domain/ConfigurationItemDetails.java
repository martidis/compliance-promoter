package com.tasosmartidis.compliancebar.features.core.model.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class ConfigurationItemDetails {
	private ConfigurationItem configurationItem;
	private Set<ComplianceShotProgress> complianceShots;
}
