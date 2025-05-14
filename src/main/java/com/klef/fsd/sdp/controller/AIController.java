package com.klef.fsd.sdp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klef.fsd.sdp.model.TaskSuggestionRequest;
import com.klef.fsd.sdp.service.PriorityService;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:2000", allowCredentials = "true")
public class AIController {

    private final PriorityService priorityService;
    
    public AIController(PriorityService priorityService) {
        this.priorityService = priorityService;
    }

    @PostMapping("/suggest-priority")
    public ResponseEntity<?> suggestPriority(@RequestBody TaskSuggestionRequest request) {
        try {
            int suggestedPriority = priorityService.suggestPriority(
                request.getTitle(), 
                request.getDescription(), 
                request.getDeadline()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("priority", suggestedPriority);
            response.put("source", "AI");
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to generate priority suggestion");
            errorResponse.put("details", e.getMessage());
            
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
        }
    }
}