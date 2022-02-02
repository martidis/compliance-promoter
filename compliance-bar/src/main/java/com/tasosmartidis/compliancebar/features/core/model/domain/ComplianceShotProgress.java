package com.tasosmartidis.compliancebar.features.core.model.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class ComplianceShotProgress implements Progressible {
	private ComplianceShot complianceShot;
	private Status status;
}
