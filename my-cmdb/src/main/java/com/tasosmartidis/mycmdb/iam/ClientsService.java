package com.tasosmartidis.mycmdb.iam;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ClientsService implements UserDetailsService {

	private final ClientsRepository clientsRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var userDetails = clientsRepository.getClientsByUsername(username);
		if(userDetails == null) {
			throw new UsernameNotFoundException("Username: '" + username + "' not found");
		}

		return userDetails;
	}
}
