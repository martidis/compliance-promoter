package com.tasosmartidis.compliancebar.features.iam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsersService implements UserDetailsService {

	@Autowired
	private UsersRepository usersRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = usersRepository.findUserEntityByUsernameIgnoreCase(username);

		if(user == null) {
			throw new UsernameNotFoundException("Username: '" + username + "' not found");
		}

		return user;
	}
}
