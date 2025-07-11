package com.metacube.userRegistrationService.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheck {
	public String getMethodName(@RequestParam String param) {
        return new String();
    }
	
	
	@GetMapping("/health-check")
	public String healthCheck() {
		return "Ok";
	}
}
