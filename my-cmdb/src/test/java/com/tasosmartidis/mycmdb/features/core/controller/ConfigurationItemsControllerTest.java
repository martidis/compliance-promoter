package com.tasosmartidis.mycmdb.features.core.controller;

import com.tasosmartidis.mycmdb.TestUtils;
import com.tasosmartidis.mycmdb.features.iam.ClientsService;
import com.tasosmartidis.mycmdb.features.core.model.api.ConfigurationItemRequest;
import com.tasosmartidis.mycmdb.features.core.model.exception.NonExistentConfigurationItemException;
import com.tasosmartidis.mycmdb.features.core.service.ConfigurationItemsService;
import com.tasosmartidis.mycmdb.features.core.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.TransactionSystemException;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;

import static com.tasosmartidis.mycmdb.TestUtils.createConfigurationItemResponseStub;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser("Compliance-Bar")
@WebMvcTest
class ConfigurationItemsControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	ConfigurationItemsService serviceMock;
	@MockBean
	TransactionSystemException transactionSystemExceptionMock;
	@MockBean
	ConstraintViolationException constraintViolationExceptionMock;
	@MockBean
	ClientsService clientsServiceMock;
	@MockBean
	RateLimitingService rateLimitingServiceMock;
	@MockBean
	Bucket bucketMock;
	@MockBean
	ConsumptionProbe consumptionProbeMock;


	@BeforeEach
	public void setup() {
		when(rateLimitingServiceMock.resolveBucket(anyString())).thenReturn(bucketMock);
		when(bucketMock.tryConsumeAndReturnRemaining(anyLong())).thenReturn(consumptionProbeMock);
		when(consumptionProbeMock.isConsumed()).thenReturn(true);
	}

	@Test
	void createConfigurationItem_whenSuccessfullyCreated_Returns200() throws Exception {
		when(serviceMock.createConfigurationItem(any(ConfigurationItemRequest.class))).thenReturn(createConfigurationItemResponseStub());
		this.mockMvc.perform(post("/api/configuration/items")
								.secure(true)
						.contentType(MediaType.APPLICATION_JSON)
						.content(TestUtils.createConfigurationItemRequestJson(3, "am@gmail.com").toString()))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	void createConfigurationItem_whenInvalidRating_Returns422() throws Exception {
		this.mockMvc.perform(post("/api/configuration/items")
						.secure(true)
						.contentType(MediaType.APPLICATION_JSON)
						.content(TestUtils.createConfigurationItemRequestJson(5, "am@gmail.com")))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void createConfigurationItem_whenInvalidRatingAndPassesController_Returns422() throws Exception {
		// since this is mocked and mock throws exception, we test exactly as above. needless but leave it to document behavior of api
		when(serviceMock.createConfigurationItem(any(ConfigurationItemRequest.class))).thenThrow(transactionSystemExceptionMock);
		when(transactionSystemExceptionMock.getRootCause()).thenReturn(constraintViolationExceptionMock);

		this.mockMvc.perform(post("/api/configuration/items")
						.secure(true)
						.contentType(MediaType.APPLICATION_JSON)
						.content(TestUtils.createConfigurationItemRequestJson(5, "am@gmail.com").toString()))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void createConfigurationItem_whenInvalidEmail_Returns422() throws Exception {
		this.mockMvc.perform(post("/api/configuration/items")
						.secure(true)
						.contentType(MediaType.APPLICATION_JSON)
						.content(TestUtils.createConfigurationItemRequestJson(3, "am_gmail.com").toString()))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void createConfigurationItem_whenInvalidEmailAndPassesController_Returns422() throws Exception {
		when(serviceMock.createConfigurationItem(any(ConfigurationItemRequest.class))).thenThrow(transactionSystemExceptionMock);
		when(transactionSystemExceptionMock.getRootCause()).thenReturn(constraintViolationExceptionMock);

		this.mockMvc.perform(post("/api/configuration/items")
						.secure(true)
						.contentType(MediaType.APPLICATION_JSON)
						.content(TestUtils.createConfigurationItemRequestJson(3, "am_gmail.com").toString()))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void createConfigurationItem_whenCINameExists_Returns422() throws Exception {
		when(serviceMock.createConfigurationItem(any(ConfigurationItemRequest.class))).thenThrow(DataIntegrityViolationException.class);

		this.mockMvc.perform(post("/api/configuration/items")
						.secure(true)
						.contentType(MediaType.APPLICATION_JSON)
						.content(TestUtils.createConfigurationItemRequestJson(3, "am@gmail.com").toString()))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void updateConfigurationItem_successful_returns200() throws Exception {
		this.mockMvc.perform(put("/api/configuration/items/4e59bd93-c902-4050-92f3-edb8c758f3f1")
						.secure(true)
						.contentType(MediaType.APPLICATION_JSON)
						.content(TestUtils.createConfigurationItemRequestJson(3, "am@gmail.com").toString()))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	void updateConfigurationItem_WhenItemNotInDB_Returns404() throws Exception {
		doThrow(NonExistentConfigurationItemException.class).when(serviceMock).updateConfigurationItem(anyString(), any(ConfigurationItemRequest.class));

		this.mockMvc.perform(put("/api/configuration/items/4e59bd93-c902-4050-92f3-edb8c758f3f1")
						.secure(true)
						.contentType(MediaType.APPLICATION_JSON)
						.content(TestUtils.createConfigurationItemRequestJson(3, "am@gmail.com").toString()))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	void updateConfigurationItem_WhenInvalidId_Returns400() throws Exception {
		this.mockMvc.perform(put("/api/configuration/items/not-a-uuid")
						.secure(true)
						.contentType(MediaType.APPLICATION_JSON)
						.content(TestUtils.createConfigurationItemRequestJson(3, "am@gmail.com")))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void updateConfigurationItem_WhenInvalidRating_Returns422() throws Exception {
		this.mockMvc.perform(put("/api/configuration/items/4e59bd93-c902-4050-92f3-edb8c758f3f1")
						.secure(true)
						.contentType(MediaType.APPLICATION_JSON)
						.content(TestUtils.createConfigurationItemRequestJson(4, "am@gmail.com")))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void updateConfigurationItem_WhenInvalidEmail_Returns422() throws Exception {
		this.mockMvc.perform(put("/api/configuration/items/4e59bd93-c902-4050-92f3-edb8c758f3f1")
						.secure(true)
						.contentType(MediaType.APPLICATION_JSON)
						.content(TestUtils.createConfigurationItemRequestJson(3, "no_gmail.com")))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void deleteConfigurationItem_WhenSuccessful_Returns200() throws Exception {
		this.mockMvc.perform(delete("/api/configuration/items/4e59bd93-c902-4050-92f3-edb8c758f3f1").secure(true))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	void deleteConfigurationItem_WhenItemNotInDB_Returns404() throws Exception {
		doThrow(NonExistentConfigurationItemException.class).when(serviceMock).deleteConfigurationItem(anyString());
		this.mockMvc.perform(delete("/api/configuration/items/4e59bd93-c902-4050-92f3-edb8c758f3f1").secure(true))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	void retrieveConfigurationItems_WhenSuccessful_Returns200() throws Exception {
		when(serviceMock.retrieveConfigurationItems(1, 1, 1)).thenReturn(new ArrayList<>());
		this.mockMvc.perform(get("/api/configuration/items").secure(true))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	void retrieveConfigurationItems_WhenInvalidFilterValues_Returns422() throws Exception {
		this.mockMvc.perform(get("/api/configuration/items?minAvailability=4&minIntegrity=2").secure(true))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void retrieveConfigurationItem_WhenItemExists_Returns200() throws Exception {
		this.mockMvc.perform(get("/api/configuration/items/4e59bd93-c902-4050-92f3-edb8c758f3f1").secure(true))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	void retrieveConfigurationItem_WhenInvalidId_Returns400() throws Exception {
		this.mockMvc.perform(get("/api/configuration/items/not-a-uuid").secure(true))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void retrieveConfigurationItem_WhenItemNotInDB_Returns404() throws Exception {
		doThrow(NonExistentConfigurationItemException.class).when(serviceMock).retrieveConfigurationItem(anyString());
		this.mockMvc.perform(get("/api/configuration/items/4e59bd93-c902-4050-92f3-edb8c758f3f1").secure(true))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	void checkThatSurpriseExceptionIsHandled_returns500_WithErrorResponse() throws Exception {
		doThrow(RuntimeException.class).when(serviceMock).retrieveConfigurationItem(anyString());
		this.mockMvc.perform(get("/api/configuration/items/4e59bd93-c902-4050-92f3-edb8c758f3f1").secure(true))
				.andDo(print())
				.andExpect(status().isInternalServerError())
				.andExpect(content().string(containsString("message")));
	}

	@Test
	void whenRateLimiterKicksIn_returnsTooManyRequests() throws Exception {
		when(rateLimitingServiceMock.resolveBucket(anyString())).thenReturn(bucketMock);
		when(bucketMock.tryConsumeAndReturnRemaining(anyLong())).thenReturn(consumptionProbeMock);
		when(consumptionProbeMock.isConsumed()).thenReturn(false);

		this.mockMvc.perform(get("/api/configuration/items/4e59bd93-c902-4050-92f3-edb8c758f3f1").secure(true))
				.andDo(print())
				.andExpect(status().isTooManyRequests())
				.andExpect(content().string(containsString("message")));
	}

}
