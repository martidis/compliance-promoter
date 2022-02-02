package com.tasosmartidis.mycmdb.features.core.service;

import com.tasosmartidis.mycmdb.TestUtils;
import com.tasosmartidis.mycmdb.features.core.model.api.ConfigurationItemRequest;
import com.tasosmartidis.mycmdb.features.core.model.entity.ConfigurationItemEntity;
import com.tasosmartidis.mycmdb.features.core.model.exception.NonExistentConfigurationItemException;
import com.tasosmartidis.mycmdb.features.core.repository.ConfigurationItemsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.cache.Cache;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ConfigurationItemServiceTest {

	private static final String CID = "ee60b900-c176-4e6f-8aa7-14d2ddb3860b";
	private static final String CI_NAME = "cool-service";
	private static final int A = 3;
	private static final int I = 2;
	private static final int C = 2;
	private static final String SYSTEM_OWNER = "cool-owner";
	private static final String TEAM_EMAIL = "cool.email@gmail.com";


	@InjectMocks
	ConfigurationItemsService configurationItemsService;

	@Mock
	ConfigurationItemsRepository repositoryMock;
	@Mock
	Cache cacheMock;

	ConfigurationItemRequest requestStub = TestUtils.createConfigurationItemRequestStub(
			CI_NAME, A, I, C, SYSTEM_OWNER, TEAM_EMAIL);
	ConfigurationItemEntity entityStub = TestUtils.createConfigurationItemEntityStub(
			CID, CI_NAME, A, I, C, SYSTEM_OWNER, TEAM_EMAIL);

	@Test
	void createConfigurationItem_callsServiceOnce() {
		when(repositoryMock.save(any(ConfigurationItemEntity.class))).thenReturn(entityStub);

		configurationItemsService.createConfigurationItem(requestStub);

		verify(repositoryMock, times(1)).save(argThat(getEntityArgumentMatcher()));
	}

	@Test
	void retrieveConfigurationItems_callsProperRepositoryMethodOnce() {
		configurationItemsService.retrieveConfigurationItems(3,3,3);

		verify(repositoryMock, times(1)).findConfigurationEntities(3,3,3);
	}

	@Test
	void retrieveConfigurationItem_callsProperRepositoryMethodOnce() {
		when(repositoryMock.findConfigurationItemEntityById(CID)).thenReturn(entityStub);

		configurationItemsService.retrieveConfigurationItem(CID);

		verify(repositoryMock, times(1)).findConfigurationItemEntityById(CID);
	}

	@Test
	void retrieveConfigurationItem_WhenIDNotKnown_throwsException() {
		Assertions.assertThrows(NonExistentConfigurationItemException.class, () -> {
			configurationItemsService.retrieveConfigurationItem(CID);
		});
	}

	@Test
	void updateConfigurationItem_callsProperMethodWithProperValues() {
		when(repositoryMock.findConfigurationItemEntityById(CID)).thenReturn(entityStub);
		when(repositoryMock.save(any(ConfigurationItemEntity.class))).thenReturn(entityStub);

		configurationItemsService.updateConfigurationItem(CID, requestStub);

		verify(repositoryMock, times(1)).save(argThat(getEntityArgumentMatcher()));
	}

	@Test
	void updateConfigurationItem_WhenItemNotKnown_throwsException() {
		Assertions.assertThrows(NonExistentConfigurationItemException.class, () -> {
			configurationItemsService.updateConfigurationItem(CID, requestStub);
		});
	}

	@Test
	void deleteConfigurationItem_callsProperRepositoryMethodOnce() {
		when(repositoryMock.findConfigurationItemEntityById(CID)).thenReturn(entityStub);

		configurationItemsService.deleteConfigurationItem(CID);

		verify(repositoryMock, times(1)).deleteById(CID);
	}

	@Test
	void deleteConfigurationItem_WhenItemNotKnown_throwsException() {

		Assertions.assertThrows(NonExistentConfigurationItemException.class, () -> {
			configurationItemsService.deleteConfigurationItem(CID);
		});
	}

	@Test
	void requestToEntity_mapsValuesProperly() {

		var itemEntity = configurationItemsService.requestToEntity.apply(UUID.randomUUID().toString(), requestStub);
		assertEquals(requestStub.getConfigurationItemName(), itemEntity.getName());
		assertEquals(requestStub.getAvailabilityRating(), itemEntity.getAvailabilityRating());
		assertEquals(requestStub.getIntegrityRating(), itemEntity.getIntegrityRating());
		assertEquals(requestStub.getConfidentialityRating(), itemEntity.getConfidentialityRating());
		assertEquals(requestStub.getSystemOwner(), itemEntity.getSystemOwner());
		assertEquals(requestStub.getTeamEmail(), itemEntity.getTeamEmail());
	}

	@Test
	void entityToResponse_mapsValuesProperly() {

		var response = configurationItemsService.entityToResponse.apply(entityStub);
		assertEquals(entityStub.getName(), response.getConfigurationItemName());
		assertEquals(entityStub.getAvailabilityRating(), response.getAvailabilityRating());
		assertEquals(entityStub.getIntegrityRating(), response.getIntegrityRating());
		assertEquals(entityStub.getConfidentialityRating(), response.getConfidentialityRating());
		assertEquals(entityStub.getSystemOwner(), response.getSystemOwner());
		assertEquals(entityStub.getTeamEmail(), response.getTeamEmail());
	}

	private ArgumentMatcher<ConfigurationItemEntity> getEntityArgumentMatcher() {
		return e -> {
			assertThat(e.getName()).isEqualTo(entityStub.getName());
			assertThat(e.getAvailabilityRating()).isEqualTo(entityStub.getAvailabilityRating());
			assertThat(e.getIntegrityRating()).isEqualTo(entityStub.getIntegrityRating());
			assertThat(e.getConfidentialityRating()).isEqualTo(entityStub.getConfidentialityRating());
			assertThat(e.getSystemOwner()).isEqualTo(entityStub.getSystemOwner());
			assertThat(e.getTeamEmail()).isEqualTo(entityStub.getTeamEmail());
			return true;
		};
	}
}
