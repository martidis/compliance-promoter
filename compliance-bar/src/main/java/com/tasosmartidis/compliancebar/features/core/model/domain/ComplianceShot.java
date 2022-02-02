package com.tasosmartidis.compliancebar.features.core.model.domain;

import com.tasosmartidis.compliancebar.features.core.model.view.Viewable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ComplianceShot implements Serializable, Viewable {

	@Getter(AccessLevel.NONE)
	private static final String RATING_VALIDATION_ERROR_MESSAGE = "Rating can be 1 (low), 2 (medium) or 3 (high)";
	@Getter(AccessLevel.NONE)
	private static final String URL_VALIDATION_ERROR_MESSAGE = "You need to provide a valid URL";
	private static final long serialVersionUID = -4565171059002287722L;

	private String id;
	@NotBlank(message = "Title cannot be empty")
	private String title;
	@NotBlank(message = "Description cannot be empty")
	@Size(max = 280, message = "Description can be up to 280 characters")
	private String shortDescription;
	@URL(message = URL_VALIDATION_ERROR_MESSAGE)
	@NotBlank(message = URL_VALIDATION_ERROR_MESSAGE)
	private String referenceUrl;
	@URL(message = URL_VALIDATION_ERROR_MESSAGE)
	@NotBlank(message = URL_VALIDATION_ERROR_MESSAGE)
	private String tutorialUrl;
	private ComplianceLevel complianceLevel;
	@Min(value = 1, message = RATING_VALIDATION_ERROR_MESSAGE)
	@Max(value = 3, message = RATING_VALIDATION_ERROR_MESSAGE)
	private Integer minAvailabilityRating;
	@Min(value = 1, message = RATING_VALIDATION_ERROR_MESSAGE)
	@Max(value = 3, message = RATING_VALIDATION_ERROR_MESSAGE)
	private Integer minIntegrityRating;
	@Min(value = 1, message = RATING_VALIDATION_ERROR_MESSAGE)
	@Max(value = 3, message = RATING_VALIDATION_ERROR_MESSAGE)
	private Integer minConfidentialityRating;
	private String createdBy;


}
