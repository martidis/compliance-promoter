package com.tasosmartidis.compliancebar.features.core.model.entity;

import com.tasosmartidis.compliancebar.features.core.model.domain.ConfigurationItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "CONFIGURATION_ITEMS")
@Table(name = "CONFIGURATION_ITEMS")
public class ConfigurationItemEntity implements Serializable {
	private static final long serialVersionUID = 3463847960006221061L;

	@Id
	@Column(name = "ID", unique = true)
	private String id;
	@Column(name = "EXTERNAL_ID")
	private String externalId;
	@Column(name = "CI_NAME", unique = true)
	private String name;
	@Column(name = "SYSTEM_OWNER")
	private String systemOwner;
	@Email(message = "Email should be valid")
	@Column(name = "TEAM_EMAIL")
	private String teamEmail;
	@Column(name = "EXTERNAL_CMDB_NAME")
	private String externalCMDBName;
	@OneToMany(
			mappedBy = "configurationItem",
			orphanRemoval = true
	)
	private Set<ComplianceShotConfigurationItemEntity> complianceShotsConfigurationItems = new HashSet<>();


	@Transient
	public ConfigurationItem entityToConfigurationItem() {
		var c = new ConfigurationItem();
		c.setId(this.getId());
		c.setConfigurationItemName(this.getName());
		c.setSystemOwner(this.getSystemOwner());
		c.setTeamEmail(this.getTeamEmail());
		c.setExternalCMDBName(this.getExternalCMDBName());
		c.setExternalCMDBId(this.getExternalId());
		return c;
	}

}

