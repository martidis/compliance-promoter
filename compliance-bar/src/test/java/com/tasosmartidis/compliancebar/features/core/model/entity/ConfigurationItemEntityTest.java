package com.tasosmartidis.compliancebar.features.core.model.entity;

import org.junit.jupiter.api.Test;

import static com.tasosmartidis.compliancebar.TestUtils.CID;
import static com.tasosmartidis.compliancebar.TestUtils.CI_NAME;
import static com.tasosmartidis.compliancebar.TestUtils.EXTERNAL_CMDB_NAME;
import static com.tasosmartidis.compliancebar.TestUtils.EXTERNAL_ID;
import static com.tasosmartidis.compliancebar.TestUtils.SYSTEM_OWNER;
import static com.tasosmartidis.compliancebar.TestUtils.TEAM_EMAIL;
import static com.tasosmartidis.compliancebar.TestUtils.createConfigurationItemEntityStub;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigurationItemEntityTest {

	@Test
	void entityToConfigurationItem() {
		var ciEntityStub = createConfigurationItemEntityStub(CID, CI_NAME, EXTERNAL_ID, EXTERNAL_CMDB_NAME, SYSTEM_OWNER, TEAM_EMAIL);
		var configurationItem = ciEntityStub.entityToConfigurationItem();
		assertEquals(CID, configurationItem.getId());
		assertEquals(CI_NAME, configurationItem.getConfigurationItemName());
		assertEquals(EXTERNAL_ID, configurationItem.getExternalCMDBId());
		assertEquals(EXTERNAL_CMDB_NAME, configurationItem.getExternalCMDBName());
		assertEquals(SYSTEM_OWNER, configurationItem.getSystemOwner());
		assertEquals(TEAM_EMAIL, configurationItem.getTeamEmail());
	}
}
