package com.metacube.serviceCatalog.controller.service;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.metacube.serviceCatalog.controller.repository.TeamRepository;
import com.metacube.serviceCatalog.controller.repository.ToolRepository;
import com.metacube.serviceCatalog.dto.MemberDTO;
import com.metacube.serviceCatalog.entites.Teams;
import com.metacube.serviceCatalog.entites.Tools;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class ToolService {
	@Autowired
	private HttpServletRequest request;
	
	//private static final Logger log = LoggerFactory.getLogger(ToolService.class);

	@Autowired
	private ToolRepository toolRepository;
	
	@Autowired
	private TeamRepository teamRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	

	
//	@Value("${user.service.userServiceUrl}")
//    private String userServiceUrl;
//
//    @Value("${user.service.adminUserName}")
//    private String adminUsername;
//
//    @Value("${user.service.adminPassword}")
//    private String adminPassword;
	
	

//	public ToolService() {
//        log.info("userServiceUrl: {}", userServiceUrl);
//        log.info("adminUsername: {}", adminUsername);
//        log.info("adminPassword: {}", adminPassword);
//    }
    
	
	
	@Transactional //adding transactional so that operations on tools and teams happen hand in hand, for eg if for some reason we are not able to assign a team to a tool, the tool will not be saved because we dont want to tool without a dev and support team assigned to it
	public Tools createTool(Tools tool) {
		System.out.println("DEBUG: createTool called, incoming toolName = " + tool.getToolName());
		
		
		//adding some debug statements that help us identify what part of our code is giving us error
		System.out.println("Tools object before save - ID: " + tool.getToolId()	+
				" , ToolName: " + tool.getToolName() + 
				" , Description: " + tool.getToolDesc() +
				" , Teams: " + tool.getTeams());
		//Saving a tool
		tool.setTeams(new ArrayList<>());
		Tools savedTool;
		
		try {
			savedTool = toolRepository.save(tool);
			System.out.println("Tool saved successfully: " + savedTool);
		} catch (Exception e) {
			System.out.println("Error saving tool: " + e.getMessage());
            e.printStackTrace();
            throw e;
		}
		
		// Fetch users from userRegistrationService
		List<MemberDTO> allMembers = new ArrayList<>(fetchUsersFromRegistrationService());
		System.out.println("All members fetched: " + allMembers);
		
		if(allMembers.isEmpty()) {
			throw new RuntimeException("No users available to assign to teams");
		}
		
		//filter all the members that registered for this tool
		
//		List<MemberDTO> registeredMembers = allMembers.stream()
//								.filter(member -> tool.getToolName()
//								.equalsIgnoreCase(member.getMemberTool()))
		allMembers.forEach(m -> System.out.println("DEBUG: Member " + m.getMemberName() + " has memberTool = " + m.getMemberTool()));
		
		List<MemberDTO> registeredMembers = new ArrayList<>(allMembers.stream()
				.filter(member -> {
				String toolName = tool.getToolName();
				String memberTool = member.getMemberTool();
				//System.out.println("DEBUG: comparing toolName=" + toolName + " to memberTool=" + memberTool + " => " + match);
				return toolName != null
			            && memberTool != null
			            && toolName.equalsIgnoreCase(memberTool);
				})
				.collect(Collectors.toList()));
		System.out.println("DEBUG: registered size = " + registeredMembers.size());

		if (registeredMembers.isEmpty()) {
            throw new RuntimeException("No users registered for tool");
        }
		
		
		// Filter users by role
		//use of .stream() ---->> converts allMembers list into a Stream<UserDTO>, which allows you to perform functional-style operations on the list, such as filtering and collecting.
 		List<MemberDTO> supportMembers = new ArrayList<>(registeredMembers.stream()
 				
				.filter(user -> "SUPPORT".equalsIgnoreCase(user.getMemberRole())) //here user is a MemberDTO object from the allMembers list.
				.collect(Collectors.toList()));
 		System.out.println("Support members: " + supportMembers);
 		List<MemberDTO> developerMembers = registeredMembers.stream()
 				.filter(user -> "DEVELOPER".equalsIgnoreCase(user.getMemberRole()))
				.collect(Collectors.toList());
 		System.out.println("Developer members: " + developerMembers);
 		

 		
 		//check user availability and give error message accordingly
 		if(supportMembers.isEmpty()) {
 			throw new RuntimeException("No SUPPORT members available to assign to support teams");
 		}
 		if(developerMembers.isEmpty()) {
 			throw new RuntimeException("No DEVELOPER members available to assign to developer teams");
 		}
 		
 		//List<Integer> assignedMemberIds = new ArrayList<>();
 		
 		//start of assigning memebrs on the basis od the tool they work on
 		
// 		//creating a list - teams that will have developer teams and support team in it
// 		List<Teams> teams = new ArrayList<>();
// 		
// 		//create support teams
// 		for(int i=1; i<=1; i++) {
// 			Teams supportTeam = new Teams();
// 			supportTeam.setTeamName("Support Team " + i + " for " + (savedTool.getToolName() != null ? savedTool.getToolName() : "Unnamed Tool"));
// 			
// 			supportTeam.setTool(savedTool);
// 			supportTeam.setSpecialisation("Support");
// 			teams.add(supportTeam);
// 		}
// 		
// 		//create developer teams
// 		for(int i=1; i<=1; i++) {
// 			Teams developerTeam = new Teams();
// 			developerTeam.setTeamName("Developer Team " + i + " for " + (savedTool.getToolName() != null ? savedTool.getToolName() : "Unnamed Tool"));
// 			
// 			developerTeam.setTool(savedTool);
// 			developerTeam.setSpecialisation("Developer");
// 			teams.add(developerTeam);
// 		}
// 		
// 		//Random methode, will help in creating random sizes of teams. makes the project look more real
// 		Random random = new Random();
// 		
// 		//assign members to teams based on role
// 		for(Teams team:teams) {
// 			team.setCreatedAt(java.time.LocalDateTime.now());
// 			
// 			//determining whether the current team members in for loop are developer or support 
// 			List<MemberDTO> eligibleMembers = "Support".equalsIgnoreCase(team.getSpecialisation()) ? supportMembers : developerMembers;
// 			System.out.println("Assigning members to team: " + team.getTeamName() + " with specialisation: " + team.getSpecialisation() + ", Eligible members: " + eligibleMembers);
// 			
// 		// Assign 2-5 random members from eligible users and creating a list of member Ids and member names
// 			List<Integer> memberIds = new ArrayList<>();
// 			List<String> memberNames = new ArrayList<>();
// 			
// 		// Copy to avoid modifying the original list, we will delete users from this copy list
//
// 			List<MemberDTO> availableUsers = eligibleMembers.stream()
// 			.filter(member -> !assignedMemberIds.contains(member.getMemberId()))
// 			.collect(Collectors.toList());
// 				
// 			
// 			
// 			int numberOfMembers = Math.min(2, availableUsers.size()); // Fixed number: 4 members, or fewer if not enough users
// 			//shows team member ids in a list
// 			for(int i=0; i<numberOfMembers; i++) {
// 				if(availableUsers.isEmpty()) break;
// 				//generating a random index of user that we need to assign to the team
// 				int randomIndex = random.nextInt(availableUsers.size());
// 				//removing a user that we will be assigning to one team so the user is not assigned again
// 				MemberDTO member = availableUsers.remove(randomIndex);
// 				memberIds.add(member.getMemberId()); 
// 				memberNames.add(member.getMemberName());
// 				assignedMemberIds.add(member.getMemberId());
// 			}
// 			team.setTeamMemberIds(memberIds);
// 			team.setTeamMemberNames(memberNames);
// 		}
 		
 		
 		//start:
 		
 		Teams supportTeam = makeTeam("Support", supportMembers, savedTool);
        Teams devTeam     = makeTeam("Developer", developerMembers,   savedTool);
        
        //save the teams
        teamRepository.saveAll(List.of(supportTeam, devTeam));
 		
 		
 		
 		//associating the tool with the team
 		savedTool.setTeams(List.of(supportTeam, devTeam));
 		return toolRepository.save(savedTool);
 		
	}
	
	private Teams makeTeam(String specialisation, List<MemberDTO> allMembers, Tools tool) {
		
		
		//in eligible members add only those members that are reg for the tool
		List<MemberDTO> eligibleMembers = allMembers.stream()
				.filter(member -> tool.getToolName()
						.equalsIgnoreCase(member.getMemberTool()))
				.filter(member -> specialisation.equalsIgnoreCase(member.getMemberRole()))
				
				.collect(Collectors.toList());

		Teams team = new Teams();
		team.setSpecialisation(specialisation);
		team.setTool(tool);
		team.setTeamName(specialisation + " Team for " + tool.getToolName());
		team.setCreatedAt(LocalDateTime.now());
		
		
		// Instead of random, assign *all* registered members for this role/tool:
		team.setTeamMemberIds(

				eligibleMembers.stream()
				.map(MemberDTO::getMemberId)
				.collect(Collectors.toList())
				
				);
		team.setTeamMemberNames(

				eligibleMembers.stream()
				.map(MemberDTO::getMemberName)
				.collect(Collectors.toList())
				);
		return team;
		
		
	}
	
	
	public List<Tools> getAllTools() {
        return toolRepository.findAll();
    }
	
	public void deleteToolByToolId(int toolId) {
		Tools tool = toolRepository.findById(toolId)
				.orElseThrow(() -> new RuntimeException("Tool not found with Id: " + toolId));
		
		// Delete associated teams first due to the bidirectional relationship
        teamRepository.deleteAll(tool.getTeams());
        
        // Delete the tool
        toolRepository.delete(tool);
		
	}
	
	//write fetch method
	
	private List<MemberDTO> fetchUsersFromRegistrationService() {
		
//		String adminUsername = "guy1@metacube.com";
//		String adminPassword = "guy1";
//		String credentials = adminUsername + ":" + adminPassword;
//		String base64Credentials = java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
//		
//		// Create HTTP headers and add the Authorization header for Basic Authentication
//		HttpHeaders headers = new HttpHeaders();
//		
//		// Add the Basic Authentication header using the Base64-encoded credentials
//		headers.add("Authorization", "Basic " + base64Credentials);
//		
		
		
		
		
		//1. grab the bearer token
//		ServletRequestAttributes attrs =
//		        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//		    HttpServletRequest incoming = attrs.getRequest();
//		    String bearer = incoming.getHeader("Authorization");
		
		
		
		String bearer = request.getHeader("Authorization");
	    if (bearer == null || !bearer.startsWith("Bearer ")) {
	        throw new RuntimeException("Missing or invalid Authorization header");
	    }
	    System.out.println("DEBUG: incoming Authorization header → " + bearer);
	    if (bearer == null || !bearer.startsWith("Bearer ")) {
	        throw new RuntimeException("Missing or invalid Authorization header");
	    }

		
//		
		// Now use that same token when calling the user‐service:
		    HttpHeaders headers = new HttpHeaders();
		    headers.set(HttpHeaders.AUTHORIZATION, bearer);
		    
		    
		    System.out.println("DEBUG: forwarding Authorization header → " 
                    + headers.getFirst(HttpHeaders.AUTHORIZATION));
		        
		// Create an HTTP entity to hold the headers (no request body needed for GET)
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		
		String userServiceUrl = "http://localhost:8080/user/allUsers";
		
		
		// Make the HTTP GET request and handle the response
        try {
            ResponseEntity<MemberDTO[]> response = restTemplate
            		.exchange(userServiceUrl, HttpMethod.GET, entity, MemberDTO[].class);
            
            System.out.println("DEBUG: status code from user service → " 
                    + response.getStatusCode().value());
            
            if(!response.getStatusCode().is2xxSuccessful()) {
            	throw new RuntimeException(
            			"User service returned HTTP " + response.getStatusCode().value()
            			);
            }
            MemberDTO[] users = response.getBody();
            
            System.out.println("DEBUG: response body → " + Arrays.toString(users));

            return users != null
                 ? Arrays.asList(users)
                 : List.of();
            
         
        } catch (RestClientResponseException rcre) {
        	 // this gives you raw status + body
        	String err = String.format(
                    "User service error %d: %s",
                    rcre.getStatusCode().value(),
                    rcre.getResponseBodyAsString()
   );
        	 System.err.println("DEBUG: RestClientResponseException → " + err);
        	 rcre.printStackTrace();
        	
        	throw new RuntimeException(err, rcre);
        
        }catch(Exception e) {
        	
        	System.err.println("DEBUG: Exception fetching users → " + e.getMessage());
            e.printStackTrace();
        	// any other errors (connection, JSON parse, etc.)
            throw new RuntimeException("Error fetching users: " + e.getMessage(), e);
        }
	}
	
	
	public List<Integer> getAllToolIds() {
	    return toolRepository.findAll()
	                         .stream()
	                         .map(Tools::getToolId)  // Assuming your entity class is `Tools` and it has `getToolId()`
	                         .collect(Collectors.toList());
	}
	
}
