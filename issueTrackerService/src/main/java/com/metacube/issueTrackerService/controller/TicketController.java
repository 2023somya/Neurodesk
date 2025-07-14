package com.metacube.issueTrackerService.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList; 

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

//import org.apache.naming.ServiceRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.metacube.issueTrackerService.config.SimilarTicketsFoundException;
import com.metacube.issueTrackerService.entity.Comment;
import com.metacube.issueTrackerService.entity.Review;
import com.metacube.issueTrackerService.entity.Status;
import com.metacube.issueTrackerService.entity.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.metacube.issueTrackerService.service.IssueService;
import com.metacube.issueTrackerService.utilis.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;





@RestController
@RequestMapping("/tickets")
//@CrossOrigin(origins = "http://localhost:3000")
public class TicketController {
	@Autowired
	private JwtUtil jwtUtil;
	
	private static final Logger logger = LoggerFactory.getLogger(TicketController.class);
	private final IssueService issueService;
	
	@Autowired
	
	public TicketController(IssueService issueService) {
		this.issueService = issueService;
	}
	
	@PostMapping("/customer/raise-ticket")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<?> create(@RequestHeader("Authorization") String token, @Valid @RequestBody Ticket ticket, BindingResult bindingResult, HttpServletRequest request){
	    if (bindingResult.hasErrors()) {
	        Map<String, String> errors = new HashMap<>();
	        bindingResult.getFieldErrors().forEach(error -> 
	            errors.put(error.getField(), error.getDefaultMessage())
	        );
	        logger.error("Validation failed for issue creation: {}", errors);
	        return ResponseEntity.badRequest().body(errors);
	    }
	    try {
	    	String cleanedToken = token.replace("Bearer", "").trim();
	        String userEmail = jwtUtil.extractUsername(cleanedToken);
	        
	    	System.out.println("User extracted from JWT: " + userEmail);
	    	
	    	ticket.setCreatedBy(userEmail);
	    	
	    	
	    	ticket.setCreatedAt(LocalDateTime.now());
	    	
	        Ticket createdTicket = issueService.createIssue(ticket, request);
	        return ResponseEntity.ok(createdTicket);
	    } catch (SimilarTicketsFoundException ex) {
	        List<Ticket> similarTickets = ex.getTickets();
	        List<Double> scores = ex.getScores();

	        List<Map<String, Object>> matches = new ArrayList<>();
	        for (int i = 0; i < similarTickets.size(); i++) {
	            Ticket t = similarTickets.get(i);
	            double score = scores.get(i);

	            Map<String, Object> info = new HashMap<>();
	            info.put("ticketId", t.getTicketId());
	            info.put("description", t.getDescription());
	            info.put("toolId", t.getToolId());
	            info.put("status", t.getStatus());
	            info.put("similarityScore", score);
	            matches.add(info);
		}
	        Map<String, Object> response = new HashMap<>();
	        response.put("message", "Similar tickets found.");
	        response.put("similar_tickets", matches);
	        response.put("action", "Would you like to comment on one of these instead of creating a new ticket?");

	        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);  
	        
	    } catch (Exception ex) {
	        logger.error("Error creating issue: {}", ex.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("error", "Internal server error: " + ex.getMessage()));
	    }
	}
	@GetMapping("/all-tickets")
	 public List<Ticket> getAllTickets() {
	     return issueService.getAllIssues();
	 }
	
	 @GetMapping("id/{id}")
	    public Ticket getTicketById(@PathVariable Integer id) {
	        return issueService.getById(id);
	    }
	 @GetMapping("/user/{createdBy}")
	 public List<Ticket> listforUser(@PathVariable String createdBy){
		 return issueService.listByUser(createdBy);
	 }
	 @GetMapping("/ping")
	 public String ping() { return "OK"; 
	 }
	 
	 @PostMapping("/ticketId/{issueId}/add-comment")
	 @PreAuthorize("hasAnyRole('SUPPORT', 'DEVELOPER', 'ADMIN')")
	 public Comment addComment(@RequestHeader("Authorization") String token, @PathVariable Integer issueId, @RequestBody @Valid Comment comment, HttpServletRequest request) {
		 logger.info("Received POST request to add comment to issue ID: {}", issueId);
		 String userEmail = extractUserEmailFromToken(token);
		 comment.setCommentedBy(userEmail);
		 comment.setCreatedAt(LocalDateTime.now());
		    return issueService.addComment(issueId, comment, request);
	 }
	 
	 @GetMapping("ticketId/{issueId}/get-comments")
	 @PreAuthorize("hasAnyRole('SUPPORT', 'DEVELOPER', 'ADMIN')")
	 public List<Comment> getCommentsForIssue(@PathVariable Integer issueId) {
	     logger.info("Received GET request to get comments for issue ID: {}", issueId);
	     return issueService.getCommentsForIssue(issueId);
	 }
	 
	 @PutMapping("ticketId/{issueId}/update-status")
	 @PreAuthorize("hasAnyRole('SUPPORT', 'DEVELOPER', 'ADMIN')")
	 public ResponseEntity<Ticket> updateIssueStatus(@RequestHeader("Authorization") String token, @PathVariable Integer issueId, @RequestBody Ticket updatedIssue) {
		 
	        Ticket existingIssue = issueService.getById(issueId);
	        

	        if (existingIssue == null) {
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found: " + issueId);
	        }
	        Status[] validStatuses = {Status.OPEN, Status.INPROGRESS, Status.RESOLVED, Status.CLOSED};
	        
	        if (updatedIssue.getStatus() != null && !List.of(validStatuses).contains(updatedIssue.getStatus())) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + updatedIssue.getStatus());
	        }
	        
	        existingIssue.setStatus(updatedIssue.getStatus());
	        
	        // Optionally update other fields if provided
	        if (updatedIssue.getTitle() != null) {
	            existingIssue.setTitle(updatedIssue.getTitle());
	        }
	        if (updatedIssue.getDescription() != null) {
	            existingIssue.setDescription(updatedIssue.getDescription());
	        }
	        if (updatedIssue.getCreatedBy() != null) {
	            existingIssue.setCreatedBy(updatedIssue.getCreatedBy());
	        }
//	        if (updatedIssue.getAssignedTo() != null) {
//	            existingIssue.setAssignedTo(updatedIssue.getAssignedTo());
//	        }
	        if (updatedIssue.getPriority() != null) {
	            existingIssue.setPriority(updatedIssue.getPriority());
	            existingIssue.setPrioritySetBy(extractUserEmailFromToken(token));
	            existingIssue.setPrioritySetAt(LocalDateTime.now());
	        }
	        
	        existingIssue.setUpdatedAt(LocalDateTime.now());

	        Ticket savedIssue = issueService.saveIssue(existingIssue); // Save updates
	        return ResponseEntity.ok(savedIssue);
	    }
	 
	 
	 @PutMapping("ticketId/{ticketId}/comment/commentId/{commentId}/update-comment")
	    @PreAuthorize("hasAnyRole('SUPPORT', 'DEVELOPER', 'ADMIN')")
	    public ResponseEntity<Comment> updateComment(@RequestHeader("Authorization") String token, @PathVariable Integer ticketId, @PathVariable Integer commentId, @RequestBody Comment updatedComment) {
	        Ticket ticket = issueService.getById(ticketId);
	        if (ticket == null) {
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found: " + ticketId);
	        }

	        Comment existingComment = ticket.getComments().stream()
	            .filter(comment -> comment.getCommentId().equals(commentId))
	            .findFirst()
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found: " + commentId));

	        // Only update text, preserve other fields unless explicitly provided
	        if (updatedComment.getComment() != null) {
	            existingComment.setComment(updatedComment.getComment());
	        }
	        existingComment.setCreatedAt(existingComment.getCreatedAt()); // Preserve original creation time
	        existingComment.setCommentedBy(existingComment.getCommentedBy()); // Preserve original commenter

	        issueService.saveIssue(ticket); // Save updates via ticket cascade
	        return ResponseEntity.ok(existingComment);
	    }
	 
	 
	 @PutMapping("ticketId/{ticketId}/setPriority")
	 @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT', 'DEVELOPER')")
	 public ResponseEntity<?> setPriority(@RequestHeader("Authorization") String token, @PathVariable Integer ticketId, @RequestBody Map<String, String> requestBody, HttpServletRequest request ){
		 try {
		        String userEmail = extractUserEmailFromToken(token);
		        String priorityy = requestBody.get("priority");
		        if (priorityy == null || priorityy.trim().isEmpty()) {
		            return ResponseEntity.badRequest().body(Map.of("error", "Priority value is required"));
		        }
		        Ticket updated = issueService.setPriority(ticketId, priorityy, userEmail);
		        return ResponseEntity.ok(updated);
		    } catch (ResponseStatusException ex) {
		        return ResponseEntity.status(ex.getStatusCode())
		                .body(Map.of("error", ex.getReason()));
		    } catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body(Map.of("error", "Unexpected server error: " + e.getMessage()));
		    }
	 }
	 
	 
	 @DeleteMapping("/delete/ticketId/{ticketId}")
	 @PreAuthorize("hasRole('ADMIN')")
	 public ResponseEntity<?> deleteTicket(@PathVariable Integer ticketId){
		 try {
		        issueService.deleteTicket(ticketId);
		        return ResponseEntity.ok("Ticket deleted successfully with ID: " + ticketId);
		    } catch (NoSuchElementException e) {
		        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket not found with ID: " + ticketId);
		    } catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting ticket: " + e.getMessage());
		    }
	 }
	 
	 
	
	 
	 @PutMapping("/support/addResolutionNote/ticketId/{ticketId}")
	 @PreAuthorize("hasRole('SUPPORT')")
	 public ResponseEntity<?> addResolutionNote(
			 @RequestHeader("Authorization") String token,
	         @PathVariable Integer ticketId,
	         @RequestBody Map<String, String> body
	 ) {
	     try {
	         Ticket ticket = issueService.getById(ticketId);
	         if (ticket == null) {
	             throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found: " + ticketId);
	         }

	         if (!Status.CLOSED.equals(ticket.getStatus())) {
	             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                     .body(Map.of("error", "Resolution note can only be added when the ticket is CLOSED"));
	         }

	         String note = body.get("note");
	         if (note == null || note.trim().isEmpty()) {
	             return ResponseEntity.badRequest()
	                     .body(Map.of("error", "Resolution note cannot be empty"));
	         }
	         String userEmail = 
	        		 extractUserEmailFromToken(token);
	         ticket.setResolutionNote(note);
	         ticket.setResolutionNoteAddedBy(userEmail);
	         ticket.setResolutionNoteAddedAt(LocalDateTime.now());

	         issueService.saveIssue(ticket);
	         return ResponseEntity.ok(ticket);

	     } catch (Exception e) {
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                 .body(Map.of("error", "Unexpected server error: " + e.getMessage()));
	     }
	 }

	 	 
	 
	
	 @PostMapping("customer/addReview/ticketId/{ticketId}")
	    @PreAuthorize("hasRole('CUSTOMER')")
	    public ResponseEntity<?> addReview(@RequestHeader("Authorization") String token, @PathVariable Integer ticketId, @RequestBody Map<String, Object> reviewData, HttpServletRequest request) {
	        Ticket ticket = issueService.getById(ticketId);
	        if (ticket == null) {
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found: " + ticketId);
	        }
	        if (!Status.CLOSED.equals(ticket.getStatus())) { // Changed to only allow CLOSED
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Review can only be added for Closed tickets");
	        }

	        String userEmail = extractUserEmailFromToken(token);
	        Review review = new Review();
	        review.setRatedByUserEmail(userEmail);
	        
	        if(reviewData.get("rating") != null) {
	        	try {
	        		int ratingValue = Integer.parseInt(reviewData.get("rating").toString());
	                if (ratingValue < 1 || ratingValue > 5) {
	                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
	                }
	                review.setRating(ratingValue);
	        	}catch (NumberFormatException e) {
	        		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be a valid integer");
				}
	        }
	        else {
	        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating is required");
	        }
	        review.setFeedback((String) reviewData.get("feedback"));
	        review.setTicket(ticket);
	        ticket.getReviews().add(review);
	        issueService.createIssue(ticket, request);
	        return ResponseEntity.ok(review);
	    }
	 
	 private String extractUserEmailFromToken(String token) {
	        if (token == null || !token.startsWith("Bearer ")) {
	            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
	        }
	        
	        String jwt = token.substring(7);
	        return jwtUtil.extractUsername(jwt); 
	    }
}
