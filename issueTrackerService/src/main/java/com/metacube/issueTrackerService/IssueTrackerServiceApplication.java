package com.metacube.issueTrackerService;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IssueTrackerServiceApplication {

	public static void main(String[] args) {
		//SpringApplication.run(IssueTrackerServiceApplication.class, args);
		
		
		// 1. Build the SpringApplication
        SpringApplication app = new SpringApplication(IssueTrackerServiceApplication.class);
        
        // 2. Override the server.port property
        app.setDefaultProperties(
            Collections.singletonMap("server.port", "8082")
        );
        
        // 3. Launch the app
        app.run(args);
	}

}
