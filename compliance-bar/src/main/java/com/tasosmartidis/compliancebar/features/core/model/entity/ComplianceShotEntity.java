package com.tasosmartidis.compliancebar.features.core.model.entity;

import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceLevel;
import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceShot;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@TypeDef(
	name = "pgsql_enum",
	typeClass = PostgreSQLEnumType.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "COMPLIANCE_SHOTS")
@Table(name = "COMPLIANCE_SHOTS")
public class ComplianceShotEntity implements Serializable {
	private static final long serialVersionUID = 8435358049517007743L;

	@Getter(AccessLevel.NONE)
	private static final String RATING_VALIDATION_ERROR_MESSAGE = "Rating can be 1 (low), 2 (medium) or 3 (high)";

	@Id
	@Column(name = "ID")
	private String id;
	@Column(name = "TITLE", unique = true)
	private String title;
	@Column(name = "SHORT_DESC")
	private String shortDescription;
	@URL
	@Column(name = "REF_URL")
	private String referenceUrl;
	@URL
	@Column(name = "TUTORIAL_URL")
	private String tutorialUrl;
	@Column(name = "COMPLIANCE_LEVEL")
	@Enumerated(EnumType.STRING)
	@Type( type = "pgsql_enum" )
	private ComplianceLevel complianceLevel;
	@Min(value = 1, message = RATING_VALIDATION_ERROR_MESSAGE)
	@Max(value = 3, message = RATING_VALIDATION_ERROR_MESSAGE)
	@Column(name = "MIN_AVAILABILITY_R")
	private Integer minAvailabilityRating;
	@Min(value = 1, message = RATING_VALIDATION_ERROR_MESSAGE)
	@Max(value = 3, message = RATING_VALIDATION_ERROR_MESSAGE)
	@Column(name = "MIN_INTEGRITY_R")
	private Integer minIntegrityRating;
	@Min(value = 1, message = RATING_VALIDATION_ERROR_MESSAGE)
	@Max(value = 3, message = RATING_VALIDATION_ERROR_MESSAGE)
	@Column(name = "MIN_CONFIDENTIALITY_R")
	private Integer minConfidentialityRating;
	@Column(name = "CREATED_BY")
	private String createdBy;
	@OneToMany(
			mappedBy = "complianceShot",
			orphanRemoval = true
	)
	private Set<ComplianceShotConfigurationItemEntity> complianceShotsConfigurationItems = new HashSet<>();

	@Transient
	public ComplianceShot entityToComplianceShot() {
		return ComplianceShot.builder()
				.id(this.getId())
				.title(this.getTitle())
				.shortDescription(this.getShortDescription())
				.referenceUrl(this.getReferenceUrl())
				.tutorialUrl(this.getTutorialUrl())
				.complianceLevel(this.getComplianceLevel())
				.minAvailabilityRating(this.getMinAvailabilityRating())
				.minIntegrityRating(this.getMinIntegrityRating())
				.minConfidentialityRating(this.getMinConfidentialityRating())
				.createdBy(this.getCreatedBy())
				.build();
	}


}
