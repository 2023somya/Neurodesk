package com.metacube.issueTrackerService.entity;

import java.util.List;

public class ToolResponse {

	private List<Integer> toolId;
	
	public ToolResponse() {}
	
	public List<Integer> getToolId() {
        return toolId;
    }
	
	public void setToolId(List<Integer> toolId) {
        this.toolId = toolId;
    }
	
}