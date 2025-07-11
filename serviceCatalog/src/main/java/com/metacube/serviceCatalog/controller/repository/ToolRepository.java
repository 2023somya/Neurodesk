package com.metacube.serviceCatalog.controller.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.metacube.serviceCatalog.entites.Teams;
import com.metacube.serviceCatalog.entites.Tools;

public interface ToolRepository extends JpaRepository<Tools, Integer>{

	
}
