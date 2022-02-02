package com.tasosmartidis.compliancebar.features.core.model.view;

import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceShot;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public class ComplianceShotsView implements Viewable {
	private Set<ComplianceShot> complianceShots;
}
