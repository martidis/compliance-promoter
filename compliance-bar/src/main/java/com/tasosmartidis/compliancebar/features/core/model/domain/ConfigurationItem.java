package com.tasosmartidis.compliancebar.features.core.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ConfigurationItem implements Serializable {
	private static final long serialVersionUID = 4098193627492282392L;

	private String id;
	private String configurationItemName;
	private String systemOwner;
	private String teamEmail;
	@JsonIgnore
	private String externalCMDBId;
	private String externalCMDBName;
}
