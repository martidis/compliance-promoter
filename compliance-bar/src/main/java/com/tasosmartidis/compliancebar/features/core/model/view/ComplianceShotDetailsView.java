package com.tasosmartidis.compliancebar.features.core.model.view;

import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceShotDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ComplianceShotDetailsView implements Viewable {
	private ComplianceShotDetails complianceShotDetails;
	private ProgressDetails progressDetails;
}
