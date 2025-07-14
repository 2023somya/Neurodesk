package com.metacube.issueTrackerService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.metacube.issueTrackerService.entity.Ticket;

public interface IssueRepo extends JpaRepository<Ticket, Integer>{
	List<Ticket> findByCreatedBy(String createdBy);
	//List<Ticket> findByAssignedTo(String assignedTo);
	//String getAssignedTo();
	//List<Ticket> findByCreatedByOrAssignedTo(String createdBy, String assignedTo);
	//List<Ticket> findByCreatedBy(String userEmail);
	List<Ticket> findByStatus(String status);
	List<Ticket> findByCreatedByAndStatus(String createdBy, String status);

}
