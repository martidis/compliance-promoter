package com.tasosmartidis.compliancebar.features.iam;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public final class SimpleGrantedAuthority implements GrantedAuthority {
	private static final long serialVersionUID = -2680225529668634424L;

	private final String role;

	@Override
	public String getAuthority() {
		return role;
	}
}
