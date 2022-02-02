package com.tasosmartidis.compliancebar.features.core.service.integrations.mycmdb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.tasosmartidis.compliancebar.TestUtils.CID;
import static com.tasosmartidis.compliancebar.TestUtils.CI_NAME;
import static com.tasosmartidis.compliancebar.TestUtils.MIN_AVAILABILITY;
import static com.tasosmartidis.compliancebar.TestUtils.MIN_CONFIDENTIALITY;
import static com.tasosmartidis.compliancebar.TestUtils.MIN_INTEGRITY;
import static com.tasosmartidis.compliancebar.TestUtils.SYSTEM_OWNER;
import static com.tasosmartidis.compliancebar.TestUtils.TEAM_EMAIL;
import static com.tasosmartidis.compliancebar.TestUtils.createMyCMDBResponse;
import static com.tasosmartidis.compliancebar.features.core.service.integrations.mycmdb.MyCMDBIntegrationService.MY_CMDB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class MyCMDBIntegrationServiceTest {

	@InjectMocks
	MyCMDBIntegrationService myCMDBIntegrationService;

	@Mock
	RestTemplate restTemplateMock;
	@Mock
	ResponseEntity responseEntityMock;

	@BeforeEach
	public void setup() {
		ReflectionTestUtils.setField(myCMDBIntegrationService, "mycmdbBaseUrl", "a-url");
	}

	@Test
	void getConfigurationItemsWithMinAIC() {
		var myCMDBResponse = createMyCMDBResponse(CID, CI_NAME, SYSTEM_OWNER, TEAM_EMAIL);
		when(responseEntityMock.getBody()).thenReturn(new MyCMDBConfigurationItemResponse[]{myCMDBResponse});
		when(restTemplateMock.exchange(
				anyString(),
				eq(HttpMethod.GET),
				any(HttpEntity.class),
				eq(MyCMDBConfigurationItemResponse[].class),
				anyInt(), anyInt(), anyInt()))
				.thenReturn(responseEntityMock);

		var actual = myCMDBIntegrationService.getConfigurationItemsWithMinAIC(MIN_AVAILABILITY, MIN_INTEGRITY, MIN_CONFIDENTIALITY);

		assertEquals(1, actual.size());
		actual.stream().forEach( ci -> {
				assertEquals(CID, ci.getExternalCMDBId());
				assertEquals(CI_NAME, ci.getConfigurationItemName());
				assertEquals(SYSTEM_OWNER, ci.getSystemOwner());
				assertEquals(TEAM_EMAIL, ci.getTeamEmail());
				assertEquals(MY_CMDB, ci.getExternalCMDBName());
			}
		);
	}

	@Test
	void getConfigurationItemsWithMinAIC_whenRequestFails_ReturnsEmptySet() {
		when(restTemplateMock.exchange(
				anyString(),
				eq(HttpMethod.GET),
				any(HttpEntity.class),
				eq(MyCMDBConfigurationItemResponse[].class),
				anyInt(), anyInt(), anyInt()))
					.thenThrow(RestClientException.class);

		var actual = myCMDBIntegrationService.getConfigurationItemsWithMinAIC(MIN_AVAILABILITY, MIN_INTEGRITY, MIN_CONFIDENTIALITY);

		assertTrue(actual.isEmpty());
	}
}
