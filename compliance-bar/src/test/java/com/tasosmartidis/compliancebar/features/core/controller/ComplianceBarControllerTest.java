package com.tasosmartidis.compliancebar.features.core.controller;

import com.tasosmartidis.compliancebar.features.core.model.api.ConfigurationItemsSelectionForShotRequest;
import com.tasosmartidis.compliancebar.features.core.model.api.ComplianceShotRequest;
import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceShot;
import com.tasosmartidis.compliancebar.features.core.service.ComplianceShotsService;
import com.tasosmartidis.compliancebar.features.core.service.RateLimitingService;
import com.tasosmartidis.compliancebar.features.core.service.ViewsConstructionService;
import com.tasosmartidis.compliancebar.features.iam.SimpleAuthenticationSuccessHandler;
import com.tasosmartidis.compliancebar.features.iam.UsersService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static com.tasosmartidis.compliancebar.TestUtils.createComplianceShotDetailsView;
import static com.tasosmartidis.compliancebar.TestUtils.createComplianceShotRequestStub;
import static com.tasosmartidis.compliancebar.TestUtils.createComplianceShotStub;
import static com.tasosmartidis.compliancebar.TestUtils.createComplianceShotsView;
import static com.tasosmartidis.compliancebar.TestUtils.createConfigurationItemDetailsView;
import static com.tasosmartidis.compliancebar.TestUtils.createConfigurationItemStub;
import static com.tasosmartidis.compliancebar.TestUtils.createConfigurationItemsView;
import static com.tasosmartidis.compliancebar.TestUtils.createDashboardViewStub;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ComplianceBarController.class)
class ComplianceBarControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	ViewsConstructionService viewsConstructionServiceMock;
	@MockBean
	ComplianceShotsService complianceShotsServiceMock;
	@MockBean
	Cache complianceBarCacheMock;
	@MockBean
	AccessDeniedHandler accessDeniedHandlerMock;
	@MockBean
	BCryptPasswordEncoder bCryptPasswordEncoderMock;
	@MockBean
	UsersService usersServiceMock;
	@MockBean
	SimpleAuthenticationSuccessHandler simpleAuthenticationSuccessHandlerMock;
	@MockBean
	RateLimitingService rateLimitingServiceMock;
	@MockBean
	Bucket bucketMock;
	@MockBean
	ConsumptionProbe consumptionProbeMock;

	@Test
	void overview_whenCalled_Responds_andTemplateViewMappingsAreCorrect() throws Exception {
		var dashboardView = createDashboardViewStub();
		when(viewsConstructionServiceMock.createDashboardView()).thenReturn(dashboardView);
		this.mockMvc
				.perform(
						get("/overview")
								.with(csrf())
								.with(user("tasos"))
				)
				.andExpect(status().isOk());
	}

	@Test
	void complianceShots_Responds_andTemplateViewMappingsAreCorrect() throws Exception {
		var shotsView = createComplianceShotsView();
		when(viewsConstructionServiceMock.createComplianceShotsOverview()).thenReturn(shotsView);
		this.mockMvc
				.perform(
						get("/compliance-shots")
								.with(csrf())
								.with(user("tasos"))
				)
				.andExpect(status().isOk());
	}

	@Test
	void complianceShotDetails_Responds_andTemplateViewMappingsAreCorrect() throws Exception {
		var complianceShotDetailsView = createComplianceShotDetailsView();
		when(viewsConstructionServiceMock.createComplianceShotDetailsView(anyString())).thenReturn(complianceShotDetailsView);
		this.mockMvc
				.perform(
						get("/compliance-shots/6a00125c-dfea-4be4-81a9-90b9a349ce65")
								.with(csrf())
								.with(user("tasos"))
				)
				.andExpect(status().isOk());
	}

	@Test
	void configurationItems_Responds_andTemplateViewMappingsAreCorrect() throws Exception {
		var configuratioItemsView = createConfigurationItemsView();
		when(viewsConstructionServiceMock.createConfigurationItemsView()).thenReturn(configuratioItemsView);
		this.mockMvc
				.perform(
						get("/configuration-items")
								.with(csrf())
								.with(user("tasos"))
				)
				.andExpect(status().isOk());
	}

	@Test
	void configurationItemDetails_Responds_andTemplateViewMappingsAreCorrect() throws Exception {
		var configurationItemDetailsView = createConfigurationItemDetailsView();
		when(viewsConstructionServiceMock.createConfigurationItemDetailsView(anyString())).thenReturn(configurationItemDetailsView);
		this.mockMvc
				.perform(
						get("/configuration-items/6a00125c-dfea-4be4-81a9-90b9a349ce65")
								.with(csrf())
								.with(user("tasos"))
				)
				.andExpect(status().isOk());
	}

	@Test
	void initiateComplianceShot_formCreation() throws Exception {
		this.mockMvc
				.perform(
						get("/compliance-shots/requests")
								.with(csrf())
								.with(user("tasos"))
				)
				.andExpect(status().isOk());
	}

	@Test
	void initiateComplianceShot_formSubmission_FieldsIncomplete_SaveShotServiceCallNotInvoked() throws Exception {
		var complianceShot = ComplianceShot.builder().build();
		this.mockMvc
				.perform(
						post("/compliance-shots/requests")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.sessionAttr("complianceShot", complianceShot)
								.with(csrf())
								.with(user("tasos"))
				)
				.andExpect(status().isOk());

		verify(complianceShotsServiceMock, times(0)).createCIsSelectionForShotRequest(any(), any());
	}

	@Test
	void initiateComplianceShot_formSubmission_ValidInput_servicesAreCalled() throws Exception {
		var configurationItem = createConfigurationItemStub("dummy-id");
		var ciSelectionRequest = new ConfigurationItemsSelectionForShotRequest(List.of(configurationItem), "request-id");
		when(complianceShotsServiceMock.createCIsSelectionForShotRequest(
				any(ComplianceShot.class), any(Authentication.class))).thenReturn(ciSelectionRequest);

		this.mockMvc
				.perform(
						post("/compliance-shots/requests")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.param("title", "title")
								.param("shortDescription", "description")
								.param("tutorialUrl", "https://www.example.com")
								.param("referenceUrl", "https://www.example.com")
								.param("minAvailabilityRating", "3")
								.param("minIntegrityRating", "3")
								.param("minConfidentialityRating", "2")
								.with(csrf())
								.with(user("tasos"))
				)
				.andExpect(status().isOk());

		verify(complianceShotsServiceMock, times(1)).complianceShotTitleExists(anyString());
		verify(complianceShotsServiceMock, times(1)).createCIsSelectionForShotRequest(any(), any());
	}

	@Test
	void initiateComplianceShot_formSubmission_TitleExists_saveActionNotCalled() throws Exception {
		when(complianceShotsServiceMock.complianceShotTitleExists(anyString())).thenReturn(true);

		this.mockMvc
				.perform(
						post("/compliance-shots/requests")
								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.param("title", "title")
								.param("shortDescription", "description")
								.param("tutorialUrl", "https://www.example.com")
								.param("referenceUrl", "https://www.example.com")
								.param("minAvailabilityRating", "3")
								.param("minIntegrityRating", "3")
								.param("minConfidentialityRating", "2")
								.with(csrf())
								.with(user("tasos"))
				)
				.andExpect(status().isOk());

		verify(complianceShotsServiceMock, times(1)).complianceShotTitleExists(anyString());
		verify(complianceShotsServiceMock, times(0)).createCIsSelectionForShotRequest(any(), any());
	}

	@Test
	void finalizeComplianceShot_callsShotCreationService_andRedirects() throws Exception {
		var requestId = "request-id";
		var ciId1 = "ci-id-1";
		var ciId2 = "ci-id-2";
		when(rateLimitingServiceMock.resolveBucket("tasos")).thenReturn(bucketMock);
		when(bucketMock.tryConsumeAndReturnRemaining(anyLong())).thenReturn(consumptionProbeMock);
		when(consumptionProbeMock.isConsumed()).thenReturn(true);

		this.mockMvc
				.perform(
						post("/compliance-shots/requests/" + requestId)
								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.param("applicableConfigurationItems[0].id", ciId1)
								.param("applicableConfigurationItems[1].id", ciId2)
								.param("complianceShotRequestId", requestId)
								.with(csrf())
								.with(user("tasos"))
				)
				.andExpect(status().is3xxRedirection());

		verify(complianceShotsServiceMock, times(1)).createComplianceShot(eq(requestId), argThat(selectedIdsInRequest(Set.of(ciId1,ciId2))));
	}

	private ArgumentMatcher<Set<String>> selectedIdsInRequest(Set<String> ids) {
		return r -> {
			r.stream().forEach(cid -> {
				assertThat(ids.contains(cid));
			});
			return true;
		};
	}

	@Test
	void finalizeComplianceShot_noItemsSelected_callsShotCreationService() throws Exception {
		var requestId = "request-id";
		var configurationItem = createConfigurationItemStub("dummy-id");
		var complianceShot = createComplianceShotStub("shot-id", 3,3,2);
		var complianceShotRequest = createComplianceShotRequestStub(Set.of(configurationItem),complianceShot);
		when(complianceBarCacheMock.get(anyString(), eq(ComplianceShotRequest.class))).thenReturn(complianceShotRequest);
		when(rateLimitingServiceMock.resolveBucket("tasos")).thenReturn(bucketMock);
		when(bucketMock.tryConsumeAndReturnRemaining(anyInt())).thenReturn(consumptionProbeMock);
		when(consumptionProbeMock.isConsumed()).thenReturn(true);

		this.mockMvc
				.perform(
						post("/compliance-shots/requests/" + requestId)
								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								// ugly way to simulate request with no ci selected
								.param("applicableConfigurationItems[0].teamEmail", "")
								.param("complianceShotRequestId", requestId)
								.with(csrf())
								.with(user("tasos"))
				)
				.andExpect(status().isOk());

		verify(complianceShotsServiceMock, times(0)).createComplianceShot(anyString(), anySet());
	}

	@Test
	void finalizeComplianceShot_whenRateLimitPassed_doesntReachShotCreationService() throws Exception {
		var requestId = "request-id";
		var ciId1 = "ci-id-1";
		var ciId2 = "ci-id-2";
		when(rateLimitingServiceMock.resolveBucket("tasos")).thenReturn(bucketMock);
		when(bucketMock.tryConsumeAndReturnRemaining(anyLong())).thenReturn(consumptionProbeMock);
		when(consumptionProbeMock.isConsumed()).thenReturn(false);

		this.mockMvc
				.perform(
						post("/compliance-shots/requests/" + requestId)
								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.param("applicableConfigurationItems[0].id", ciId1)
								.param("applicableConfigurationItems[1].id", ciId2)
								.param("complianceShotRequestId", requestId)
								.with(csrf())
								.with(user("tasos"))
				)
				.andExpect(status().isOk());

		verify(complianceShotsServiceMock, times(0)).createComplianceShot(anyString(), anySet());
	}
}
