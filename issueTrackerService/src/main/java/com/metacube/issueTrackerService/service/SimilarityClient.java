package com.metacube.issueTrackerService.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SimilarityClient {

	@Value("${similarity.service.url}")
    private String similarityServiceUrl;

	
	public SimilarTicket getSimilarity(String newTicket, List<String> pastTickets){
			    
	    
		String url = similarityServiceUrl + "/similarity";
		RestTemplate restTemplate = new RestTemplate();
		
		 Map<String, Object> payload = new HashMap<>();
		 
		 payload.put("new_ticket", newTicket);
		    payload.put("past_tickets", pastTickets);
		    
		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);//tells server we are sending json data
		    
		    
		    HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
		    
		    ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

		    List<Double> scores = (List<Double>) response.getBody().get("scores");
		    
		    List<Integer> matchingIndexes = new ArrayList<>();
		    List<Double> matchingScores = new ArrayList<>();
		    
		    double threshold = 0.5; 

		    for (int i = 0; i < scores.size(); i++) {
		    	if (scores.get(i) >= threshold) {
		            matchingIndexes.add(i);
		            matchingScores.add(scores.get(i));
		        }
		    }

		    return new SimilarTicket(matchingIndexes, matchingScores);

	}
	
	//DTO
	public class SimilarTicket {
		private List<Integer> matchingTickets;
	    private List<Double> matchingScores;
	    
	    public SimilarTicket(List<Integer> matchingTickets, List<Double> matchingScores) {
	        this.matchingTickets = matchingTickets;
	        this.matchingScores = matchingScores;
	    }
	    
		public List<Integer> getMatchingTickets() {
			return matchingTickets;
		}
		public void setMatchingTickets(List<Integer> matchingTickets) {
			this.matchingTickets = matchingTickets;
		}
		public List<Double> getMatchingScores() {
			return matchingScores;
		}
		public void setMatchingScores(List<Double> matchingScores) {
			this.matchingScores = matchingScores;
		}

	    
	}
}
