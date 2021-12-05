package com.tasosmartidis.mycmdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.tasosmartidis.mycmdb.core.model.api.ConfigurationItemRequest;
import com.tasosmartidis.mycmdb.core.model.api.ConfigurationItemResponse;
import com.tasosmartidis.mycmdb.core.model.entity.ConfigurationItemEntity;

public class TestUtils {
	private static final ObjectWriter OBJECT_WRITTER;

	static {
		OBJECT_WRITTER = new ObjectMapper().writer().withDefaultPrettyPrinter();
	}

	public static String createConfigurationItemRequestJson(Integer aicRating, String email) throws JsonProcessingException {
		var ci = ConfigurationItemRequest.builder()
				.configurationItemName("apollo")
				.availabilityRating(aicRating)
				.integrityRating(aicRating)
				.confidentialityRating(aicRating)
				.teamEmail(email)
				.build();

		return OBJECT_WRITTER.writeValueAsString(ci);
	}

	public static ConfigurationItemResponse createConfigurationItemResponseStub() {
		return new ConfigurationItemResponse(
				"1bee8932-adb9-4dc1-a43c-3655a69da47c",
				"someapp",
				3,
				3,
				3,
				"Alan Turing",
				"alan.turing@h.com"
		);
	}

	public static ConfigurationItemRequest createConfigurationItemRequestStub(String ciName, int availabilityRating,
																			  int integrityRating, int confidentialityRating, String systemOwner, String email) {
		return ConfigurationItemRequest.builder()
				.configurationItemName(ciName)
				.availabilityRating(availabilityRating)
				.integrityRating(integrityRating)
				.confidentialityRating(confidentialityRating)
				.systemOwner(systemOwner)
				.teamEmail(email)
				.build();
	}

	public static ConfigurationItemEntity createConfigurationItemEntityStub(String id, String ciName, int availabilityRating,
													int integrityRating, int confidentialityRating, String systemOwner, String email) {
		return new ConfigurationItemEntity(
				id,
				ciName,
				availabilityRating,
				integrityRating,
				confidentialityRating,
				systemOwner,
				email);
	}
}
