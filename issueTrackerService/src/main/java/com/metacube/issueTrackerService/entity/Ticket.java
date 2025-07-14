package com.metacube.issueTrackerService.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.CascadeType;

//import java.io.ObjectInputFilter.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "tickets")


public class Ticket {

	
	@Id
	@Column(name = "ticket_id")
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@JsonProperty(value = "issueId", access = Access.READ_ONLY)
	private Integer ticketId;
	
	@NotBlank(message = "Title must not be blank")
	@Column(name = "title")
	private String title;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "created_By", nullable = false)
	//@NotBlank(message = "CreatedBy must not be blank")
	private String createdBy;
	
//	@Column(name = "assigned_To", nullable = false)
//	@NotBlank(message = "AssignedTo must not be blank")
//	private String assignedTo;
	
	

	@OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
   // @JoinColumn(name = "ticket_id")
	@JsonManagedReference
    private List<Review> reviews = new ArrayList<>();
	
	
	private String prioritySetBy;
    private LocalDateTime prioritySetAt;
	
	
    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
	
	  public String getPrioritySetBy() {
		return prioritySetBy;
	}

	public void setPrioritySetBy(String prioritySetBy) {
		this.prioritySetBy = prioritySetBy;
	}

	public LocalDateTime getPrioritySetAt() {
		return prioritySetAt;
	}

	public void setPrioritySetAt(LocalDateTime prioritySetAt) {
		this.prioritySetAt = prioritySetAt;
	}

	@Enumerated(EnumType.STRING)
	  @Column(
	    name = "status",
	    nullable = false,
	    length = 20  // force a string column
	  )
	  
	//@Enumerated(EnumType.STRING)
	private Status status = Status.OPEN;
	
	  
//	  public Ticket(String title, String description, String createdBy, String assignedTo, Status status, Long issueId) {
//	        this.ticketId = ticketId;
//	        this.title = title;
//	        this.description = description;
//	        this.createdBy = createdBy;
//	        this.assignedTo = assignedTo;
//	        this.status = status;
//	    }

	    @PrePersist
	    public void onPrePersist() {
	        System.out.println("PrePersist: Issue ID = " + ticketId);
	    }
	
	@OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
//	@JoinColumn("ticket_id")
	@JsonManagedReference
	private List<Comment> comments = new ArrayList<> ();
	
	private String priority;
	
	
	private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Column(name = "resolution_note")
    private String resolutionNote;
    
    @Column(name = "note_added_by")
    private String resolutionNoteAddedBy;
    
    public String getResolutionNoteAddedBy() {
		return resolutionNoteAddedBy;
	}
	public void setResolutionNoteAddedBy(String resolutionNoteAddedBy) {
		this.resolutionNoteAddedBy = resolutionNoteAddedBy;
	}
	public LocalDateTime getResolutionNoteAddedAt() {
		return resolutionNoteAddedAt;
	}
	public void setResolutionNoteAddedAt(LocalDateTime resolutionNoteAddedAt) {
		this.resolutionNoteAddedAt = resolutionNoteAddedAt;
	}

	@Column(name = "note_added_at")
	private LocalDateTime resolutionNoteAddedAt;

    
    
    public String getResolutionNote() {
		return resolutionNote;
	}
	public void setResolutionNote(String resolutionNote) {
		this.resolutionNote = resolutionNote;
	}
	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Integer getToolId() {
		return toolId;
	}

	public void setToolId(Integer toolId) {
		this.toolId = toolId;
	}

	private Integer toolId;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	//@JsonProperty("issueId")
	  public Integer getTicketId() {
	        return ticketId;
	    }
	  //@JsonProperty(value="issueId", access = Access.READ_ONLY)
	    public void setTicketId(Integer ticketId) {
	        this.ticketId = ticketId;
	    }
	    
	    public String getTitle() {
	        return title;
	    }

	    public void setTitle(String title) {
	        this.title = title;
	    }
	    
	    public String getCreatedBy() {
	        return createdBy;
	    }

	    public void setCreatedBy(String createdBy) {
	        this.createdBy = createdBy;
	    }
	    
//	    public String getAssignedTo() {
//	        return assignedTo;
//	    }
//
//	    public void setAssignedTo(String assignedTo) {
//	        this.assignedTo = assignedTo;
//	    }
	    
	    public Status getStatus() {
	        return status;
	    }

	    public void setStatus(Status status) {
	        this.status = status;
	    }
	
}
