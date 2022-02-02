package com.tasosmartidis.compliancebar.features.core.model.entity;

import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceShotConfigurationItemProgress;
import com.tasosmartidis.compliancebar.features.core.model.domain.Status;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.io.Serializable;

@TypeDef(
	name = "pgsql_enum",
	typeClass = PostgreSQLEnumType.class)
@Data
@Entity(name = "COMPLIANCE_SHOTS_CONFIGURATION_ITEMS")
@Table(name = "COMPLIANCE_SHOTS_CONFIGURATION_ITEMS")
public class ComplianceShotConfigurationItemEntity implements Serializable {
	private static final long serialVersionUID = 2062737526094055109L;

	@EmbeddedId
	ComplianceShotConfigurationItemKey id;
	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	@Type( type = "pgsql_enum" )
	private Status status;

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
	@MapsId("configurationItemId")
	@JoinColumn(name = "CONFIGURATION_ITEM_ID")
	private ConfigurationItemEntity configurationItem;

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
	@MapsId("complianceShotId")
	@JoinColumn(name = "COMPLIANCE_SHOT_ID")
	private ComplianceShotEntity complianceShot;

	public ComplianceShotConfigurationItemProgress entityToComplianceShotConfigurationItemProgress() {
		return ComplianceShotConfigurationItemProgress.builder()
				.complianceShotId(this.getComplianceShot().getId())
				.configurationItemId(this.getConfigurationItem().getId())
				.status(this.getStatus())
				.build();
	}

}
