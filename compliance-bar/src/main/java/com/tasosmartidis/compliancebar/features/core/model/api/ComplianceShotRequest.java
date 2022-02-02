package com.tasosmartidis.compliancebar.features.core.model.api;

import com.tasosmartidis.compliancebar.features.core.model.domain.ConfigurationItem;
import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceShot;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ComplianceShotRequest implements Serializable {
	private static final long serialVersionUID = 1563460697079881928L;

	private ComplianceShot complianceShot;
	private Set<ConfigurationItem> applicableConfigurationItems;

}
