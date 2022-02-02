package com.tasosmartidis.compliancebar.features.core.model.entity;

import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceLevel;
import org.junit.jupiter.api.Test;

import static com.tasosmartidis.compliancebar.TestUtils.CREATED_BY;
import static com.tasosmartidis.compliancebar.TestUtils.MIN_AVAILABILITY;
import static com.tasosmartidis.compliancebar.TestUtils.MIN_CONFIDENTIALITY;
import static com.tasosmartidis.compliancebar.TestUtils.MIN_INTEGRITY;
import static com.tasosmartidis.compliancebar.TestUtils.REF_URL;
import static com.tasosmartidis.compliancebar.TestUtils.SHOT_DESC;
import static com.tasosmartidis.compliancebar.TestUtils.SHOT_ID;
import static com.tasosmartidis.compliancebar.TestUtils.SHOT_TITLE;
import static com.tasosmartidis.compliancebar.TestUtils.TUT_URL;
import static com.tasosmartidis.compliancebar.TestUtils.createComplianceShotEntityStub;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ComplianceShotEntityTest {

	@Test
	void entityToComplianceShot() {
		var shotEntityStub = createComplianceShotEntityStub(SHOT_ID, ComplianceLevel.BLOCKING, SHOT_TITLE, SHOT_DESC, MIN_AVAILABILITY,
																MIN_INTEGRITY, MIN_CONFIDENTIALITY, TUT_URL, REF_URL, CREATED_BY);

		var complianceShot = shotEntityStub.entityToComplianceShot();
		assertEquals(SHOT_ID, complianceShot.getId());
		assertEquals(SHOT_TITLE, complianceShot.getTitle());
		assertEquals(SHOT_DESC, complianceShot.getShortDescription());
		assertEquals(MIN_AVAILABILITY, complianceShot.getMinAvailabilityRating());
		assertEquals(MIN_INTEGRITY, complianceShot.getMinIntegrityRating());
		assertEquals(MIN_CONFIDENTIALITY, complianceShot.getMinConfidentialityRating());
		assertEquals(TUT_URL, complianceShot.getTutorialUrl());
		assertEquals(REF_URL, complianceShot.getReferenceUrl());
		assertEquals(CREATED_BY, complianceShot.getCreatedBy());
		assertEquals(ComplianceLevel.BLOCKING, complianceShot.getComplianceLevel());
	}
}
