package com.tasosmartidis.compliancebar.features.core.model.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class ConfigurationItemProgress implements Progressible {
	private ConfigurationItem configurationItem;
	private Status status;
}
