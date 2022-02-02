package com.tasosmartidis.compliancebar.features.iam;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

	@GetMapping("/login")
	public String viewLoginPage() {
		return "login";
	}

}
