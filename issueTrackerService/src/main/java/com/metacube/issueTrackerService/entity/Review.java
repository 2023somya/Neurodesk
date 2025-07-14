package com.metacube.issueTrackerService.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "reviews")
public class Review {

	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer reviewId;
	 	
	 	private String ratedByUserEmail;

	 	@Min(value = 1, message = "Rating must be at least 1")
	    @Max(value = 5, message = "Rating must not exceed 5")
	 	private Integer rating;
	 	
	 	private String feedback;
	 	
	 	public String getFeedback() {
			return feedback;
		}

		public void setFeedback(String feedback) {
			this.feedback = feedback;
		}

		public Integer getReviewId() {
			return reviewId;
		}

		public void setReviewId(Integer reviewId) {
			this.reviewId = reviewId;
		}

		public String getRatedByUserEmail() {
			return ratedByUserEmail;
		}

		public void setRatedByUserEmail(String ratedByUserEmail) {
			this.ratedByUserEmail = ratedByUserEmail;
		}

		

		public Integer getRating() {
			return rating;
		}

		public void setRating(Integer rating) {
			this.rating = rating;
		}

		public Ticket getTicket() {
			return ticket;
		}

		public void setTicket(Ticket ticket) {
			this.ticket = ticket;
		}

		@ManyToOne
	    @JoinColumn(name = "ticket_id", nullable = false)
		@JsonBackReference
	    private Ticket ticket;
}
