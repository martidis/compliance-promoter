package com.tasosmartidis.compliancebar.features.core.service.integrations;

import com.tasosmartidis.compliancebar.features.core.model.domain.ConfigurationItem;
import com.tasosmartidis.compliancebar.features.core.model.exception.ComplianceShotCreationException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import static com.hazelcast.internal.util.QuickMath.bytesToHex;

public interface CMDBIntegration {

	Set<ConfigurationItem> getConfigurationItemsWithMinAIC(Integer availabilityRating,
														   Integer integrityRating,
														   Integer confidentialityRating);

	default String createKey(String externalId, String externalCMDBName) {
		try {
			var digest = MessageDigest.getInstance("SHA-256");
			byte[] hashbytes = digest.digest((externalId + externalCMDBName).getBytes(StandardCharsets.UTF_8));
			return bytesToHex(hashbytes);
		} catch (NoSuchAlgorithmException e) {
			throw new ComplianceShotCreationException("Couldn't calculate hash for configuration item id");
		}
	}

}
