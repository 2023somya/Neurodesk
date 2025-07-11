package com.metacube.userRegistrationService.config;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UserEntryConfig implements CommandLineRunner{
	
	@Autowired
	private DataSource dataSource;
	
	@Override
	public void run(String... args) throws Exception{
		try (Connection conn = dataSource.getConnection()) {
            System.out.println("Successfully connected to the database!");
        }
	}

}
