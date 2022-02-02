package com.tasosmartidis.compliancebar.features.iam;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class UsersServiceTest {

	String username = "alan";

	@InjectMocks
	UsersService usersService;
	@Mock
	UsersRepository repositoryMock;
	@Mock
	UserEntity userEntityMock;

	@Test
	void loadUserByUsername_WhenUsernamePresent_returnsUser() {
		when(repositoryMock.findUserEntityByUsernameIgnoreCase(username)).thenReturn(userEntityMock);

		usersService.loadUserByUsername(username);

		verify(repositoryMock).findUserEntityByUsernameIgnoreCase(username);
	}

	@Test
	void loadUserByUsername_WhenUsernameNotPresent_ThrowsException() {
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			usersService.loadUserByUsername(username);
			verify(repositoryMock).findUserEntityByUsernameIgnoreCase(username);
		});
	}
}
