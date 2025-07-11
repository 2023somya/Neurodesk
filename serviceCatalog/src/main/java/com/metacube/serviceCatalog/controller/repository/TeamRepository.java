package com.metacube.serviceCatalog.controller.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.metacube.serviceCatalog.entites.Teams;

public interface TeamRepository extends JpaRepository<Teams, Integer>{

}
