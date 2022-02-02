package com.tasosmartidis.compliancebar.features.core.service.integrations.mycmdb;

import com.tasosmartidis.compliancebar.features.core.model.domain.ConfigurationItem;
import com.tasosmartidis.compliancebar.features.core.service.integrations.CMDBIntegration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MyCMDBIntegrationService implements CMDBIntegration {
	static final String MY_CMDB = "MyCMDB";
	private final String mycmdbBaseUrl;
	private final RestTemplate restTemplate;

	public MyCMDBIntegrationService(@Value("${mycmdb.url}") String mycmdbBaseUrl,
									@Qualifier("myCMDB") RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		this.mycmdbBaseUrl = mycmdbBaseUrl;
	}

	@Override
	public Set<ConfigurationItem> getConfigurationItemsWithMinAIC(Integer availabilityRating,
																  Integer integrityRating,
																  Integer confidentialityRating) {

		MyCMDBConfigurationItemResponse[] items = null;
		try{
			var response =  restTemplate.exchange(
										mycmdbBaseUrl,
										HttpMethod.GET,
										new HttpEntity<>(new HttpHeaders()),
										MyCMDBConfigurationItemResponse[].class,
										availabilityRating,
										integrityRating,
										confidentialityRating);
			items = response.getBody();

		}catch (RestClientException ex) {
			// we just log the error. It can be that we have other integrations and those work
			// in error scenario here, we simply do not have CIs from this integration
			// use case: if no integrations fetch items, we cant create compliance shot as it requires
			// at least one CI. Show error then
			log.error("Couldn't retrieve items from mycmdb. Error: {}", ex.getMessage());
		}


		if(items == null) {
			return Collections.unmodifiableSet(new HashSet<>());
		}

		return Arrays.stream(items).map(i-> {
					var ci = new ConfigurationItem();
					ci.setId(createKey(i.getId(), MY_CMDB));
					ci.setExternalCMDBId(i.getId());
					ci.setConfigurationItemName(i.getConfigurationItemName());
					ci.setSystemOwner(i.getSystemOwner());
					ci.setTeamEmail(i.getTeamEmail());
					ci.setExternalCMDBName(MY_CMDB);
					return ci;
		}).collect(Collectors.toUnmodifiableSet());
	}


}
