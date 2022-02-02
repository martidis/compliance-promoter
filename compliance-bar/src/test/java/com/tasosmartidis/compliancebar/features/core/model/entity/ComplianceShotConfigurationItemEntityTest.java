package com.tasosmartidis.compliancebar.features.core.model.entity;

import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceLevel;
import com.tasosmartidis.compliancebar.features.core.model.domain.Status;
import org.junit.jupiter.api.Test;

import static com.tasosmartidis.compliancebar.TestUtils.CID;
import static com.tasosmartidis.compliancebar.TestUtils.CI_NAME;
import static com.tasosmartidis.compliancebar.TestUtils.CREATED_BY;
import static com.tasosmartidis.compliancebar.TestUtils.EXTERNAL_CMDB_NAME;
import static com.tasosmartidis.compliancebar.TestUtils.EXTERNAL_ID;
import static com.tasosmartidis.compliancebar.TestUtils.MIN_AVAILABILITY;
import static com.tasosmartidis.compliancebar.TestUtils.MIN_CONFIDENTIALITY;
import static com.tasosmartidis.compliancebar.TestUtils.MIN_INTEGRITY;
import static com.tasosmartidis.compliancebar.TestUtils.REF_URL;
import static com.tasosmartidis.compliancebar.TestUtils.SHOT_DESC;
import static com.tasosmartidis.compliancebar.TestUtils.SHOT_ID;
import static com.tasosmartidis.compliancebar.TestUtils.SHOT_TITLE;
import static com.tasosmartidis.compliancebar.TestUtils.SYSTEM_OWNER;
import static com.tasosmartidis.compliancebar.TestUtils.TEAM_EMAIL;
import static com.tasosmartidis.compliancebar.TestUtils.TUT_URL;
import static com.tasosmartidis.compliancebar.TestUtils.createComplianceShotEntityStub;
import static com.tasosmartidis.compliancebar.TestUtils.createConfigurationItemEntityStub;
import static com.tasosmartidis.compliancebar.TestUtils.createCsCIStub;
import static org.junit.jupiter.api.Assertions.*;

class ComplianceShotConfigurationItemEntityTest {

	@Test
	void entityToComplianceShotConfigurationItemProgress() {
		var shotEntityStub = createComplianceShotEntityStub(SHOT_ID, ComplianceLevel.BLOCKING, SHOT_TITLE, SHOT_DESC, MIN_AVAILABILITY,
				MIN_INTEGRITY, MIN_CONFIDENTIALITY, TUT_URL, REF_URL, CREATED_BY);
		var ciEntityStub = createConfigurationItemEntityStub(CID, CI_NAME, EXTERNAL_ID, EXTERNAL_CMDB_NAME, SYSTEM_OWNER, TEAM_EMAIL);

		var expectedStatus = Status.CANCELLED;
		var csciEntity = createCsCIStub(shotEntityStub, ciEntityStub, expectedStatus);

		var csciProgress = csciEntity.entityToComplianceShotConfigurationItemProgress();

		assertEquals(CID, csciProgress.getConfigurationItemId());
		assertEquals(SHOT_ID, csciProgress.getComplianceShotId());
		assertEquals(expectedStatus, csciProgress.getStatus());
	}
}
