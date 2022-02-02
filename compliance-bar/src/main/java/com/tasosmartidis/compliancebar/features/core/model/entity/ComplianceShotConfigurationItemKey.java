package com.tasosmartidis.compliancebar.features.core.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class ComplianceShotConfigurationItemKey implements Serializable  {
	private static final long serialVersionUID = 476927125824876025L;

	@Column(name = "CONFIGURATION_ITEM_ID")
	String configurationItemId;

	@Column(name = "COMPLIANCE_SHOT_ID")
	String complianceShotId;


}
