package com.metacube.issueTrackerService.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
//import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.hibernate.id.insert.AbstractReturningDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;

import com.metacube.issueTrackerService.config.RestClientConfig;
import com.metacube.issueTrackerService.config.SimilarTicketsFoundException;
import com.metacube.issueTrackerService.entity.Comment;
import com.metacube.issueTrackerService.entity.Status;
import com.metacube.issueTrackerService.entity.Ticket;
import com.metacube.issueTrackerService.entity.ToolResponse;
import com.metacube.issueTrackerService.repository.CommentRepo;
import com.metacube.issueTrackerService.repository.IssueRepo;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class IssueService {
	private static final Logger logger = LoggerFactory.getLogger(IssueService.class);
	private final IssueRepo repo;
	private final CommentRepo commentRepo;
	private final RestTemplate restTemplate;
	
	private final String userServiceUrl = "http://localhost:8080/user"; 
	
	@Autowired
	private SimilarityClient similarityClient;

	@Autowired
	public IssueService(
			IssueRepo repo,
			CommentRepo commentRepo,
			RestTemplate restTemplate,
			@Value("${user.service.url}") String userServiceUrl 
			) {
		this.repo = repo;
		this.commentRepo = commentRepo;
		this.restTemplate = restTemplate;
		//this.userServiceUrl = userServiceUrl;
	}
	
	@Bean
    public RestTemplate rest(RestTemplateBuilder builder) {
        return builder
            .setConnectTimeout(Duration.ofSeconds(5)) // Example: 5-second connect timeout
            .setReadTimeout(Duration.ofSeconds(10))  // Example: 10-second read timeout
            .build();
    }
	
	
	public Ticket createIssue (Ticket ticket, HttpServletRequest request) {
		logger.info("Creating issue: {}", ticket);
		

		if (ticket.getCreatedBy() == null || ticket.getCreatedBy().trim().isEmpty()) {
            logger.error("CreatedBy is null or empty: {}", ticket);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CreatedBy must not be blank");
        }
		

	    // Generate issueId if not provided
		validateUser(ticket.getCreatedBy(), null, "createdBy", request);
//        if (ticket.getAssignedTo() != null) {
//            validateUser(ticket.getAssignedTo(), "assignedTo");
//        }
        if (ticket.getToolId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ToolId must be provided");
        }
        List<Integer> availableTools = fetchToolsFromToolService();
        
        if (!availableTools.contains(ticket.getToolId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid toolId: " + ticket.getToolId());
        }
        
        List<Ticket> openTickets = repo.findByCreatedBy(ticket.getCreatedBy())
        		.stream()
        		.filter(t -> t.getStatus() == Status.OPEN)
        		.collect(Collectors.toList());
        List<String> openDescriptions = openTickets.stream().map(Ticket::getDescription).toList();
        
        SimilarityClient.SimilarTicket result = similarityClient.getSimilarity(ticket.getDescription(), openDescriptions);
     // ✅ Step 6: If any matching tickets found, stop and return them
        if (!result.getMatchingTickets().isEmpty()) {
            List<Ticket> matchingTickets = new ArrayList<>();
            List<Double> matchingScores = result.getMatchingScores();
            if (matchingScores == null || matchingScores.isEmpty()) {
                return repo.save(ticket);
            }
            for (Integer index : result.getMatchingTickets()) {
                matchingTickets.add(openTickets.get(index));
            }

            throw new SimilarTicketsFoundException(matchingTickets, matchingScores);
        }
        
        
        logger.info("Saving issue to database");
        Ticket savedTicket = repo.save(ticket);
        logger.info("Issue saved successfully: {}", savedTicket);
        //logger.info("Saved issue comments: {}", savedIssue.getComments());
        return savedTicket;
    }
	
	public List<Ticket> getAllIssues() {
        return repo.findAll();
    }
	
	public Ticket saveIssue(Ticket issue) {
        logger.info("Saving issue: {}", issue);
        return repo.save(issue);
    }
	
	public Ticket setPriority(Integer ticketId, String priority, String setBy) {
	    Ticket ticket = repo.findById(ticketId)
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

	    ticket.setPriority(priority);
	    ticket.setPrioritySetBy(setBy);
	    ticket.setPrioritySetAt(LocalDateTime.now());

	    return repo.save(ticket);
	}
	
	
//	public Issue getById(String issueId) {
////		return repo.findById(id)
////				.orElseThrow(() -> new ResponseStatusException(
////						HttpStatus.NOT_FOUND, "Issue not found: " + id));
//		try {
//            return repo.findById(issueId)
//                       .orElseThrow(() -> 
//                           new ResponseStatusException(
//                               HttpStatus.NOT_FOUND,
//                               "Issue not found: " + issueId
//                           )
//                       );
//        } 
//        // Re–throw your 404 as‐is
//        catch (ResponseStatusException e) {
//            throw e;
//        } 
//        // Wrap anything else in a 500
//        catch (Exception e) {
//            throw new ResponseStatusException(
//                HttpStatus.INTERNAL_SERVER_ERROR,
//                "Error retrieving issue: " + issueId,
//                e
//            );
//        }
//    }

	
	public Ticket getById(Integer issueId) {
        try {
            
            return repo.findById(issueId)
                       .orElseThrow(() -> 
                           new ResponseStatusException(
                               HttpStatus.NOT_FOUND,
                               "Issue not found: " + issueId
                           )
                       );
        } catch (NumberFormatException e) {
            logger.error("Invalid issueId format: {}", issueId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid issueId format: " + issueId, e);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error retrieving issue: " + issueId,
                e
            );
        }
    }
		
	
	
	public List<Ticket> listByUser(String createdBy){
		return repo.findByCreatedBy(createdBy);
	}
	
	public Comment addComment (Integer issueId, Comment comment, HttpServletRequest request) {
		logger.info("Adding comment to issue ID: {}", issueId);
		Ticket issue = repo.findById(issueId)
						  . orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Issue not found with ID: " + issueId));
		validateUser(comment.getCommentedBy(), null, "commentedBy", request);
		comment.setIssue(issue);
		comment.setCreatedAt(LocalDateTime.now());
		Comment savedComment = commentRepo.save(comment);
        logger.info("Comment added successfully: {}", savedComment);
        return savedComment;
	}
	
	
	
	
	private void validateUser(String email, String userId, String fieldName, HttpServletRequest request) {
		
	
        try {
        	String url = "http://localhost:8080/user/email/" + email;
        	
//            RestTemplate restTemplate = new RestTemplate();
//
    	    String jwtToken = request.getHeader("Authorization");// ✅ No error now
    	    
    	    if (jwtToken == null || !jwtToken.startsWith("Bearer ")) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
            }
//
   		HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", request.getHeader("Authorization")); // ✅ forward token
//    	    
    	    HttpEntity<Void> entity = new HttpEntity<>(headers);
//            ResponseEntity<String> response = restTemplate.exchange(userServiceUrl, HttpMethod.GET, entity, String.class);
//        	
        	
        	ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                   entity,
                    Void.class
                );
//            logger.debug("Validating {}: {}", fieldName, userId);
//            restTemplate.exchange(
//                    userServiceUrl + "/email/" + userId,
//                    HttpMethod.GET,
//                    null,
//                    Void.class
//            );
//            logger.debug("Validated {}: {}", fieldName, userId);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Validation failed for {}: received status {}", fieldName, response.getStatusCode());
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error validating " + fieldName);
            }
        } catch (HttpClientErrorException.NotFound nf) {
            logger.error("User not found for {}: {}", fieldName, userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found for " + fieldName + ": " + userId);
        } catch (ResourceAccessException rae) {
            logger.error("Cannot reach User Service at {}: {}", userServiceUrl, rae.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Cannot reach User Service at " + userServiceUrl, rae);
        } catch (Exception e) {
            logger.error("Unexpected error validating {}: {}", fieldName, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error validating " + fieldName, e);
        }
    }
	
	
	
	public List<Comment> getCommentsForIssue(Integer issueId) {
	    logger.info("Fetching comments for issue ID: {}", issueId);
	    if (!repo.existsById(issueId)) {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found with ID: " + issueId);
	    }
	    return commentRepo.findByIssue_TicketId(issueId);
	}
	
	public void deleteTicket(Integer ticketId) {
	    Ticket ticket = repo.findById(ticketId)
	                        .orElseThrow(() -> new NoSuchElementException("Ticket not found with ID: " + ticketId));
	    repo.delete(ticket);
	}
	private List<Integer> fetchToolsFromToolService() {
	    String url = "http://localhost:8081/tools/ids"; // <- This line

        try {
        	ResponseEntity<List<Integer>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Integer>>() {}
                );
            if (response.getStatusCode() == HttpStatus.OK) {
                List<Integer> toolIds = response.getBody();

                if (toolIds == null || toolIds.isEmpty()) {
                    logger.warn("Tool service returned empty tool list");
                    return List.of();
                }
                return toolIds;
                
            } else {
                logger.error("Failed to fetch tools from ToolService: {}", response.getStatusCode());
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Cannot fetch tools from ToolService");
            }
        } catch (Exception e) {
            logger.error("Error connecting to ToolService at {}: {}", userServiceUrl, e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error connecting to ToolService", e);
        }
    }
	
	
}
