package com.metacube.serviceCatalog.entites;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "tools")
public class Tools {
	
	public int getToolId() {
		return toolId;
	}

	public void setToolId(int toolId) {
		this.toolId = toolId;
	}

	public String getToolName() {
		return toolName;
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int toolId;
	
	@Column(name = "tool_name")
	@NotBlank(message = "tool name can not be empty")
	private String toolName;
	
	@NotBlank(message = "Description cannot be empty")
	private String toolDesc;
	
	@OneToMany(mappedBy = "tool", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonManagedReference //without this the postman output leads to a recursive output because put tools entity has a teams field and our teams entity has a tools field, they both keep getting reassigned.
	private List<Teams> teams = new ArrayList<>();
	
	
	public List<Teams> getTeams() {
	    return teams;
	}

	public void setTeams(List<Teams> teams) {
		this.teams.clear(); // Avoid duplicates
        if (teams != null) {
            this.teams.addAll(teams);
        }
	}

	public String getToolDesc() {
		return toolDesc;
	}

	public void setToolDesc(String toolDesc) {
		this.toolDesc = toolDesc;
	}
	
}
