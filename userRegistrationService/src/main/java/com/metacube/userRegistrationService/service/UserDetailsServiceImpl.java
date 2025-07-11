package com.metacube.userRegistrationService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.metacube.userRegistrationService.controller.entity.UserEntry;
import com.metacube.userRegistrationService.repository.UserEntryRepository;

@Component
public class UserDetailsServiceImpl implements UserDetailsService{
	@Autowired
	private UserEntryRepository repo;
	
	@Autowired
	private UserManagementService userService;
	
	
	
	@Override
	public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException{
		UserEntry user = repo.findByUserEmail(userEmail);
		if(user != null) {
			UserDetails userDetails=org.springframework.security.core.userdetails.User.builder()
			.username(user.getUserEmail())
			.password(user.getUserPassword())
		    .roles(user.getUserRole().name())
		//	.authorities("ROLE_" + user.getUserRole().name())
			. build();
			
			return userDetails;
		}
		
		throw new UsernameNotFoundException("User not found with email id: " + userEmail);
	}

}