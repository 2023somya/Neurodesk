package com.metacube.issueTrackerService.config;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.hibernate.id.insert.AbstractReturningDelegate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(5)) // 5-second connect timeout
                .setReadTimeout(Duration.ofSeconds(10))  // 10-second read timeout
                .build();
	}
	
}
