package com.tasosmartidis.mycmdb.features.core.model.api;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ConfigurationItemResponse implements Serializable  {
	private static final long serialVersionUID = 220147563130778987L;

	private String id;
	private String configurationItemName;
	private Integer availabilityRating;
	private Integer integrityRating;
	private Integer confidentialityRating;
	private String systemOwner;
	private String teamEmail;
}
