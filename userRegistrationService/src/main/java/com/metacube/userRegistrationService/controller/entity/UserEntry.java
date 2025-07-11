package com.metacube.userRegistrationService.controller.entity;


import java.security.PrivateKey;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntry {

	
	@Id
	//generated value auto generates the id nums
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;
	
	@Column(name = "user_name")
	private String userName;
	
	@Column(name = "user_role")
	@Enumerated(EnumType.STRING)
	private Role userRole;
	
	
	@Column(name = "user_email")
	private String userEmail;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	@Column(name = "user_pass")
	private String userPassword;
	
	@Column(name = "user_tool")
	private String userTool;
	
	


	public String getUserTool() {
		return userTool;
	}

	public void setUserTool(String userTool) {
		this.userTool = userTool;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	

	public int getUserId() {
		return userId;
		
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Role getUserRole() {
		return userRole;
	}

	public void setUserRole(Role userRole) {
		this.userRole = userRole;
	}
}
