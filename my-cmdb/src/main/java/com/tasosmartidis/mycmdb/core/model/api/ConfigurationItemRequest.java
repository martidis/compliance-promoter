package com.tasosmartidis.mycmdb.core.model.api;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Builder
@ToString
public class ConfigurationItemRequest {
	@Getter(AccessLevel.NONE)
	private static final String RATING_VALIDATION_ERROR_MESSAGE = "Rating can be 1 (low), 2 (medium) or 3 (high)";

	private String configurationItemName;
	@Min(value = 1, message = RATING_VALIDATION_ERROR_MESSAGE)
	@Max(value = 3, message = RATING_VALIDATION_ERROR_MESSAGE)
	private Integer availabilityRating;
	@Min(value = 1, message = RATING_VALIDATION_ERROR_MESSAGE)
	@Max(value = 3, message = RATING_VALIDATION_ERROR_MESSAGE)
	private Integer integrityRating;
	@Min(value = 1, message = RATING_VALIDATION_ERROR_MESSAGE)
	@Max(value = 3, message = RATING_VALIDATION_ERROR_MESSAGE)
	private Integer confidentialityRating;
	private String systemOwner;
	@Email(message = "Email should be valid")
	private String teamEmail;
}
