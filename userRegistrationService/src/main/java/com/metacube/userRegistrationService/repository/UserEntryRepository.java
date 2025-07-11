package com.metacube.userRegistrationService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;

import com.metacube.userRegistrationService.controller.entity.UserEntry;

public interface UserEntryRepository extends JpaRepository<UserEntry, Integer> {
  UserEntry	 findByUserEmail(String userEmail);
  UserEntry	 findByUserName(String userName);
	
}
