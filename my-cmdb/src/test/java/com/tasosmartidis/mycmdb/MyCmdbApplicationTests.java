package com.tasosmartidis.mycmdb;

import com.tasosmartidis.mycmdb.features.core.model.api.ConfigurationItemRequest;
import com.tasosmartidis.mycmdb.features.core.model.api.ConfigurationItemResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyCmdbApplicationTests {

	private static final int A = 3;
	private static final int I = 2;
	private static final int C = 2;
	private static final String SYSTEM_OWNER = "integration-test-owner";
	private static final String TEAM_EMAIL = "integration-test@gmail.com";

	@LocalServerPort
	Integer port;

	@Autowired
	RestTemplate restTemplate;

	@Qualifier("ciRepo")
	@Autowired
	CrudRepository crudRepository;

	@Test
	void retrievingAllConfigurationItems_retrievesResponseWith200() {
		var allEntitiesResponse = restTemplate.exchange("https://localhost:" + port + "/api/configuration/items", HttpMethod.GET, null, new ParameterizedTypeReference<List<ConfigurationItemResponse>>() {});

		assertEquals(200, allEntitiesResponse.getStatusCodeValue());
	}

	@Test
	void retrievingAllConfigurationItems_retrievesQueriedItemsOnlyWith200() {
		crudRepository.deleteAll();
		var entity222 = TestUtils.createConfigurationItemEntityStub("eb450b87-724a-4ebd-b69a-d7092d14e6d5", "stretch-app", 2, 2, 2, SYSTEM_OWNER, TEAM_EMAIL);
		crudRepository.save(entity222);
		var entity332 = TestUtils.createConfigurationItemEntityStub("0bf8bf88-71fc-4e2e-b537-4164cbbed3cd", "dust-app", 3, 3, 2, SYSTEM_OWNER, TEAM_EMAIL);
		crudRepository.save(entity332);

		var allEntitiesResponse = restTemplate.exchange("https://localhost:" + port + "/api/configuration/items?minAvailability=3", HttpMethod.GET, null, new ParameterizedTypeReference<List<ConfigurationItemResponse>>() {});
		assertEquals(200, allEntitiesResponse.getStatusCodeValue());
		assertEquals(1, allEntitiesResponse.getBody().size());
	}

	@Test
	void retrievingAllConfigurationItems_WithInvalidQueryParams_Returns422() {
		HttpClientErrorException clientErrorExceptionThrown = assertThrows(HttpClientErrorException.class, () -> {
			restTemplate.exchange("https://localhost:" + port + "/api/configuration/items?minIntegrity=4", HttpMethod.GET, null, String.class);
		});

		assertEquals(422, clientErrorExceptionThrown.getRawStatusCode());

	}

	@Test
	void createConfigurationItem_whenSuccessfullyCreated_Returns200() throws Exception {
		var request = TestUtils.createConfigurationItemRequestStub(
				"anyone-app", A, I, C, SYSTEM_OWNER, TEAM_EMAIL);
		var httpEntity = new HttpEntity(request);

		var response = restTemplate.exchange("https://localhost:" + port + "/api/configuration/items", HttpMethod.POST, httpEntity, ConfigurationItemResponse.class);
		assertEquals(200, response.getStatusCodeValue());
		assertRequestFieldsMatchResponseFields(request, response.getBody());
	}

	@Test
	void createConfigurationItem_whenInvalidEmal_Returns422() throws Exception {
		var request = TestUtils.createConfigurationItemRequestStub(
				"heaven-app", A, I, C, SYSTEM_OWNER, "not-an-email");
		var httpEntity = new HttpEntity(request);

		HttpClientErrorException clientErrorExceptionThrown = assertThrows(HttpClientErrorException.class, () -> {
			restTemplate.exchange("https://localhost:" + port + "/api/configuration/items", HttpMethod.POST, httpEntity, ConfigurationItemResponse.class);

		});

		assertEquals(422, clientErrorExceptionThrown.getRawStatusCode());
	}

	@Test
	void createConfigurationItem_whenInvalidRating_Returns422() throws Exception {
		var request = TestUtils.createConfigurationItemRequestStub(
				"melt-app", A, I, 4, SYSTEM_OWNER, TEAM_EMAIL);
		var httpEntity = new HttpEntity(request);

		HttpClientErrorException clientErrorExceptionThrown = assertThrows(HttpClientErrorException.class, () -> {
			restTemplate.exchange("https://localhost:" + port + "/api/configuration/items", HttpMethod.POST, httpEntity, ConfigurationItemResponse.class);
		});

		assertEquals(422, clientErrorExceptionThrown.getRawStatusCode());
	}

	@Test
	void createConfigurationItem_whenNameExists_Returns422() throws Exception {
		var ciId = "009df390-1916-44d1-9fe1-7ef9ad1ca62b";
		var existingCIName = "lessen-app";
		var entity = TestUtils.createConfigurationItemEntityStub(ciId, existingCIName, A, I, C, SYSTEM_OWNER, TEAM_EMAIL);
		crudRepository.save(entity);
		var request = TestUtils.createConfigurationItemRequestStub(
				existingCIName, A, I, 4, SYSTEM_OWNER, TEAM_EMAIL);
		var httpEntity = new HttpEntity(request);

		HttpClientErrorException clientErrorExceptionThrown = assertThrows(HttpClientErrorException.class, () -> {
			restTemplate.exchange("https://localhost:" + port + "/api/configuration/items", HttpMethod.POST, httpEntity, ConfigurationItemResponse.class);
		});

		assertEquals(422, clientErrorExceptionThrown.getRawStatusCode());
	}

	@Test
	void updateConfigurationItem_successful_returns200() throws Exception {
		var cid = "bdb8a92e-3f35-4423-a7ed-db80c3313d7a";
		var entity = TestUtils.createConfigurationItemEntityStub(cid, "improve-app", A, I, C, SYSTEM_OWNER, TEAM_EMAIL);
		crudRepository.save(entity);
		var updateRequest = TestUtils.createConfigurationItemRequestStub(
				"improve-app", A, I, C, SYSTEM_OWNER, TEAM_EMAIL);
		var httpEntity = new HttpEntity(updateRequest);

		var response = restTemplate.exchange("https://localhost:" + port + "/api/configuration/items/" + cid, HttpMethod.PUT, httpEntity, ConfigurationItemResponse.class);
		assertEquals(200, response.getStatusCodeValue());
	}

	@Test
	void updateConfigurationItem_WhenItemNotInDB_Returns404() throws Exception {
		var unknownId = "a9db8dac-b398-46d4-b2fb-25f47011374b";
		var updateRequest = TestUtils.createConfigurationItemRequestStub(
				"choose-app", A, I, C, SYSTEM_OWNER, TEAM_EMAIL);
		var httpEntity = new HttpEntity(updateRequest);

		HttpClientErrorException clientErrorExceptionThrown = assertThrows(HttpClientErrorException.class, () -> {
			restTemplate.exchange("https://localhost:" + port + "/api/configuration/items/" + unknownId, HttpMethod.PUT, httpEntity, ConfigurationItemResponse.class);
		});

		assertEquals(404, clientErrorExceptionThrown.getRawStatusCode());
	}

	@Test
	void deleteConfigurationItem_WhenSuccessful_Returns200() throws Exception {
		var cid = "bdb8a92e-3f35-4423-a7ed-db80c3313d7a";
		var entity = TestUtils.createConfigurationItemEntityStub(cid, "improve-app", A, I, C, SYSTEM_OWNER, TEAM_EMAIL);
		crudRepository.save(entity);

		var entityResponse = restTemplate.exchange("https://localhost:" + port + "/api/configuration/items/" + cid, HttpMethod.DELETE, new HttpEntity(null), String.class);

		assertEquals(200, entityResponse.getStatusCodeValue());
	}

	@Test
	void deleteConfigurationItem_WhenItemNotInDB_Returns404() throws Exception {
		var unknownId = "a9db8dac-b398-46d4-b2fb-25f47011374b";

		HttpClientErrorException clientErrorExceptionThrown = assertThrows(HttpClientErrorException.class, () -> {
			restTemplate.exchange("https://localhost:" + port + "/api/configuration/items/" + unknownId, HttpMethod.DELETE, new HttpEntity(null), ResponseEntity.class);

		});

		assertEquals(404, clientErrorExceptionThrown.getRawStatusCode());
	}

	@Test
	void retrieveConfigurationItem_WhenItemExists_Returns200() throws Exception {
		var cid = "f40e80de-3f40-4c14-a58a-48c56ae658e3";
		var entity = TestUtils.createConfigurationItemEntityStub(cid, "imagine-app", A, I, C, SYSTEM_OWNER, TEAM_EMAIL);
		crudRepository.save(entity);
		var entityResponse = restTemplate.exchange("https://localhost:" + port + "/api/configuration/items/" + cid, HttpMethod.GET, null, ConfigurationItemResponse.class);

		assertEquals(200, entityResponse.getStatusCodeValue());
	}

	@Test
	void retrieveConfigurationItem_WhenInvalidId_Returns400() throws Exception {

		HttpClientErrorException clientErrorExceptionThrown = assertThrows(HttpClientErrorException.class, () -> {
			restTemplate.exchange("https://localhost:" + port + "/api/configuration/items/not-a-uuid", HttpMethod.GET, null, ConfigurationItemResponse.class);

		});

		assertEquals(400, clientErrorExceptionThrown.getRawStatusCode());
	}

	@Test
	void retrieveConfigurationItem_WhenItemNotInDB_Returns404() throws Exception {
		var unknownId = "a9db8dac-b398-46d4-b2fb-25f47011374b";

		HttpClientErrorException clientErrorExceptionThrown = assertThrows(HttpClientErrorException.class, () -> {
			restTemplate.exchange("https://localhost:" + port + "/api/configuration/items/" + unknownId, HttpMethod.GET, null, ConfigurationItemResponse.class);
		});

		assertEquals(404, clientErrorExceptionThrown.getRawStatusCode());
	}

	private void assertRequestFieldsMatchResponseFields(ConfigurationItemRequest request, ConfigurationItemResponse response) {
		assertEquals(request.getConfigurationItemName(), response.getConfigurationItemName());
		assertEquals(request.getAvailabilityRating(), response.getAvailabilityRating());
		assertEquals(request.getIntegrityRating(), response.getIntegrityRating());
		assertEquals(request.getConfidentialityRating(), response.getConfidentialityRating());
		assertEquals(request.getSystemOwner(), response.getSystemOwner());
		assertEquals(request.getTeamEmail(), response.getTeamEmail());

	}

}
