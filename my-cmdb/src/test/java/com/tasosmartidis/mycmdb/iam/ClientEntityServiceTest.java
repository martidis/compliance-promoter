package com.tasosmartidis.mycmdb.iam;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ClientEntityServiceTest {

	String username = "alan";

	@InjectMocks
	ClientsService clientsService;
	@Mock
	ClientsRepository repositoryMock;
	@Mock
	ClientEntity clientEntityMock;

	@Test
	void loadUserByUsername_WhenUsernamePresent_returnsUser() {
		when(repositoryMock.getClientsByUsername(username)).thenReturn(clientEntityMock);

		clientsService.loadUserByUsername(username);

		verify(repositoryMock).getClientsByUsername(username);
	}

	@Test
	void loadUserByUsername_WhenUsernameNotPresent_ThrowsException() {
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			clientsService.loadUserByUsername(username);
			verify(repositoryMock).getClientsByUsername(username);
		});
	}
}
