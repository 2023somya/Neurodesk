package com.metacube.serviceCatalog.entites;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "teams")

public class Teams {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int teamId;
	
	@Column
	private String teamName;
	
	@Column
	private String Specialisation;
	


	
	public int getTeamId() {
		return teamId;
	}

	public String getSpecialisation() {
		return Specialisation;
	}

	public void setSpecialisation(String specialisation) {
		Specialisation = specialisation;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public List<String> getTeamMemberNames() {
		return teamMemberNames;
	}

	public void setTeamMemberNames(List<String> teamMemberNames) {
		this.teamMemberNames = teamMemberNames;
	}

	public Tools getTool() {
		return tool;
	}

	public void setTool(Tools tool) {
		this.tool = tool;
	}

	public List<Integer> getTeamMemberIds() {
		return teamMemberIds;
	}

	public void setTeamMemberIds(List<Integer> teamMemberIds) {
		this.teamMemberIds = teamMemberIds;
	}


	@Column(name="created_at")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
	
	@ElementCollection
	@CollectionTable(name = "team_member_names", joinColumns = @JoinColumn(name = "team_id"))
    @Column(name = "member_name")
	private List<String> teamMemberNames;
	
	@ManyToOne  //many teams mapped to one tool
	@JoinColumn(name = "tool_id", nullable = false)
	@JsonBackReference
	private Tools tool;
	
	@ElementCollection
	@CollectionTable(name = "team_members", joinColumns = @JoinColumn(name = "team_id"))
	@Column(name = "user_id")
	//read use of these 3 annotations
	
	private List<Integer> teamMemberIds;

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
		
	}
	
}
