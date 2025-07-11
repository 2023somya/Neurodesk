package com.metacube.userRegistrationService.controller;

//import java.awt.desktop.UserSessionEvent;
//import java.net.PasswordAuthentication;
import java.util.List;

//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.core.Authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.context.SecurityContextHolder;


import com.metacube.userRegistrationService.controller.entity.UserEntry;
import com.metacube.userRegistrationService.repository.UserEntryRepository;
import com.metacube.userRegistrationService.service.UserDetailsServiceImpl;
import com.metacube.userRegistrationService.service.UserManagementService;
import com.metacube.userRegistrationService.service.utilis.JwtUtil;

import lombok.extern.slf4j.Slf4j;


@RestController
@Slf4j //to print login message
@RequestMapping("/user")
public class UserEntryController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserEntryRepository repo;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private UserManagementService service;
	
	@Autowired
	private UserManagementService userManagementService;

	
	@GetMapping("/allUsers")
	public ResponseEntity<List<UserEntry>> getAllUsers() {
//        List<UserEntry> userEntry = service.getAllUsers();
//        return ResponseEnatity.ok(userEntry);
	
	// setting jwt login
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userEmail = authentication.getName();
		//UserEntry me = repo.findByUserEmail(userEmail);
		UserEntry me = repo.findByUserEmail(userEmail);
		List<UserEntry> userList =  service.getAllUsers();
		System.out.println("DEBUG: Fetching all users for " + userEmail + ": " + userList);
		if (userList != null && !userList.isEmpty()) {
			return new ResponseEntity<>(userList, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
	
	 
	@PostMapping("/signup")
	public ResponseEntity<UserEntry> registerUser(@RequestBody UserEntry userEntry) {
		
		try {
			UserEntry savedUser = service.createUser(userEntry);
					
					// Return 201 Created with the saved user
					
			return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
		}catch (IllegalArgumentException e) {
			// Return 400 Bad Request if validation fails
			return ResponseEntity.badRequest().body(null);
		}
		
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> loginUser(@RequestBody UserEntry userEntry) {
		
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(userEntry.getUserEmail(), userEntry.getUserPassword()));
					
					UserDetails userDetails = userDetailsService.loadUserByUsername(userEntry.getUserEmail());
				    UserEntry user = repo.findByUserEmail(userEntry.getUserEmail()); // new

				    if (user == null) {
			            return new ResponseEntity<>("User not found", HttpStatus.UNAUTHORIZED);
			        }
			        String role = user.getUserRole().name(); // Example: "ADMIN"

		String jwt =jwtUtil.generateToken(userDetails.getUsername(), role);
		 System.out.println("JWT Token: " + jwt);
					return new ResponseEntity<>(jwt, HttpStatus.OK);
		
					
		}catch (Exception e) {
			// Return 400 Bad Request if validation fails
			 e.printStackTrace();
			return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@GetMapping("/email/{userEmail}")
	public ResponseEntity<?> getUserByEmail(@PathVariable String userEmail) {
	    UserEntry user = userManagementService.findByUserEmail(userEmail); // or repo directly
	    if (user == null) {
	        return ResponseEntity.notFound().build();
	    }
	    return ResponseEntity.ok().build(); // Just to validate existence
	}
	
	
	
	@DeleteMapping("/del/{userId}")
	public ResponseEntity<String> removeUser (@PathVariable int userId) {
		
		// delegate service to delete user
		
		try {
		service.deleteUser(userId);
		return ResponseEntity.ok("User with User Id: " + userId + " deleted successfully!");
		}catch(IllegalArgumentException e) {
			// Return 404 Not Found if user doesn’t exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
		
	}
	
}
