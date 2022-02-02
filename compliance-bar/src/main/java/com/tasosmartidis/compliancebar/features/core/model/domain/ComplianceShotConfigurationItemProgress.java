package com.tasosmartidis.compliancebar.features.core.model.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
@EqualsAndHashCode
public class ComplianceShotConfigurationItemProgress implements Serializable, Progressible {
	private static final long serialVersionUID = -385998594223620503L;

	private Status status;
	private String configurationItemId;
	private String complianceShotId;


}
