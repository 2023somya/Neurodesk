package com.metacube.issueTrackerService.config;

import java.util.List;


import com.metacube.issueTrackerService.entity.Ticket;


public class SimilarTicketsFoundException extends RuntimeException{
	
    private static final long serialVersionUID = 1L;

	private List<Ticket> tickets;
    private List<Double> scores;

    public SimilarTicketsFoundException(List<Ticket> tickets, List<Double> scores) {
        super("Similar tickets found.");
        this.tickets = tickets;
        this.scores = scores;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public List<Double> getScores() {
        return scores;
    }
}
