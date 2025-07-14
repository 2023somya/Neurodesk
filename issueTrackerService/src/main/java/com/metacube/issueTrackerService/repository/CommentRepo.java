package com.metacube.issueTrackerService.repository;

import java.util.List;
import com.metacube.issueTrackerService.entity.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;

public interface CommentRepo extends JpaRepository<Comment, Long>{
	List<Comment> findByIssue_TicketId(Integer issueId);
}
