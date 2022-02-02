package com.tasosmartidis.compliancebar.features.core.service.integrations.mycmdb;

import lombok.Data;

@Data
public class MyCMDBConfigurationItemResponse {

	private String id;
	private String configurationItemName;
	private Integer availabilityRating;
	private Integer integrityRating;
	private Integer confidentialityRating;
	private String systemOwner;
	private String teamEmail;
}
