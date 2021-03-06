package com.tasosmartidis.compliancebar.features.iam;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SimpleAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


	@Override
	public void onAuthenticationSuccess(HttpServletRequest arg0, HttpServletResponse arg1, Authentication authentication)
			throws IOException {
		var redirectStrategy = new DefaultRedirectStrategy();
		redirectStrategy.sendRedirect(arg0, arg1, "/overview");

	}

}
