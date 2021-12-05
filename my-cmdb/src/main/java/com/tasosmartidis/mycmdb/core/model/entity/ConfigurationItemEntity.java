package com.tasosmartidis.mycmdb.core.model.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "CONFIGURATION_ITEMS")
public class ConfigurationItemEntity implements Serializable {
	private static final long serialVersionUID = 7467395533105922577L;

	@Getter(AccessLevel.NONE)
	private static final String RATING_VALIDATION_ERROR_MESSAGE = "Rating can be 1 (low), 2 (medium) or 3 (high)";

	@Id
	@Column(name = "ID")
	private String id;
	@Column(name = "CI_NAME", unique = true)
	private String name;
	@Min(value = 1, message = RATING_VALIDATION_ERROR_MESSAGE)
	@Max(value = 3, message = RATING_VALIDATION_ERROR_MESSAGE)
	@Column(name = "AVAILABILITY_R")
	private Integer availabilityRating;
	@Min(value = 1, message = RATING_VALIDATION_ERROR_MESSAGE)
	@Max(value = 3, message = RATING_VALIDATION_ERROR_MESSAGE)
	@Column(name = "INTEGRITY_R")
	private Integer integrityRating;
	@Min(value = 1, message = RATING_VALIDATION_ERROR_MESSAGE)
	@Max(value = 3, message = RATING_VALIDATION_ERROR_MESSAGE)
	@Column(name = "CONFIDENTIALITY_R")
	private Integer confidentialityRating;
	@Column(name = "SYSTEM_OWNER")
	private String systemOwner;
	@Email(message = "Email should be valid")
	@Column(name = "TEAM_EMAIL")
	private String teamEmail;

}
