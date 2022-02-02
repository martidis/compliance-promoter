package com.tasosmartidis.mycmdb.features.iam;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public final class SimpleGrantedAuthority implements GrantedAuthority {

	private final String role;

	@Override
	public String getAuthority() {
		return role;
	}
}
