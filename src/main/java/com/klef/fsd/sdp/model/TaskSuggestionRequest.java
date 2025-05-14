package com.klef.fsd.sdp.model;

public class TaskSuggestionRequest {
    private String title;
    private String description;
    private String deadline;
    
    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
}
