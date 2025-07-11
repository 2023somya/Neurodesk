package com.metacube.serviceCatalog.controller;

import java.security.PrivateKey;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metacube.serviceCatalog.controller.service.ToolService;
import com.metacube.serviceCatalog.entites.Tools;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tools")
public class ToolController {
	

	@Autowired
	private ToolService toolService;
	
	@PostMapping("/create")
	
	public ResponseEntity<Tools> createTool(@Valid @RequestBody Tools tool){
		Tools createdTool = toolService.createTool(tool);
		return new ResponseEntity<>(createdTool, HttpStatus.CREATED);
	}
	
	@GetMapping
    public ResponseEntity<List<Tools>> getAllTools() {
        List<Tools> tools = toolService.getAllTools();
        return new ResponseEntity<>(tools, HttpStatus.OK);
    }
	
	
	@GetMapping("/ids")
	public ResponseEntity<List<Integer>> getAllToolIds() {
	    List<Integer> toolIds = toolService.getAllToolIds(); // We'll add this in ToolService
	    return new ResponseEntity<>(toolIds, HttpStatus.OK);
	}
	
	@DeleteMapping("/del/{toolId}")
	public ResponseEntity<Void> deleteToolByToolId(@PathVariable int toolId){
		toolService.deleteToolByToolId(toolId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		

}
