package com.metacube.userRegistrationService.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.metacube.userRegistrationService.controller.entity.Role;
import com.metacube.userRegistrationService.controller.entity.UserEntry;
import com.metacube.userRegistrationService.repository.UserEntryRepository;



@Service
public class UserManagementService {

	@Autowired
	private UserEntryRepository repo;
	
	@Autowired
	private  PasswordEncoder passwordEncoder;
	
	public UserEntry createUser(UserEntry userEntry) {
		
		if (userEntry.getUserName() == null || userEntry.getUserName().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (userEntry.getUserEmail() == null || userEntry.getUserEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (userEntry.getUserPassword() == null || userEntry.getUserPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (userEntry.getUserRole() == null) {
            throw new IllegalArgumentException("User role cannot be null");
        }
        if (repo.findByUserEmail(userEntry.getUserEmail()) != null) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (repo.findByUserName(userEntry.getUserName()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
		
		
		userEntry.setUserPassword(passwordEncoder.encode(userEntry.getUserPassword()));
		
		return repo.save(userEntry);
		
	}
	
	public List<UserEntry> getAllUsers(){
		return repo.findAll();
	}
	
	public void deleteUser(int userId) {
		if(!repo.existsById(userId)) {
			throw new IllegalArgumentException("User not found with user Id: " + userId);
		}
		repo.deleteById(userId);
	}
	
	
	
		public UserEntry findByUserEmail(String userEmail) {
	        UserEntry user = repo.findByUserEmail(userEmail);
	        if (user == null) {
	            throw new UsernameNotFoundException("No user found with email: " + userEmail);
	        }
	        return user;
	    }
	
}
