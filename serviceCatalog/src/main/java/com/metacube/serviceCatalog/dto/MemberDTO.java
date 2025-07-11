package com.metacube.serviceCatalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

//used so that we can transfer only those member fields that we want to expose, like if we dont want to expose user passowor we dont need to mention it

public class MemberDTO {

	
	private Integer memberID;
	private String memberName;
	private String memberRole;
	private String memberEmail;
	private String memberTool;
	
	@JsonProperty("userTool")
	public String getMemberTool() {
		return memberTool;
	}
	
	@JsonProperty("userTool")
	public void setMemberTool(String memberTool) {
		this.memberTool = memberTool;
	}
	@JsonProperty("userId")
	public Integer getMemberId() {
		return memberID;
	}
	@JsonProperty("userId")
	public void setMemberID(Integer memberID) {
		this.memberID = memberID;
	}
	
	@JsonProperty("userName")
	public String getMemberName() {
		return memberName;
	}
	
	@JsonProperty("userName")
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	
	@JsonProperty("userRole")
	public String getMemberRole() {
		return memberRole;
	}
	@JsonProperty("userRole")
	public void setMemberRole(String memberRole) {
		this.memberRole = memberRole;
	}
	@JsonProperty("userEmail")
	public String getMemberEmail() {
		return memberEmail;
	}
	@JsonProperty("userEmail")
	public void setMemberEmail(String memberEmail) {
		this.memberEmail = memberEmail;
	}
	
		

	@Override
    public String toString() {
        return "MemberDTO{memberId=" + memberID + ", memberName='" + memberName + "', memberRole='" + memberRole + "'," + "memberTool=' " + memberTool + "'}";
    }
}
