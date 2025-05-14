package com.klef.fsd.sdp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AdvancedPriorityService implements PriorityService {
    
    private static final Logger logger = LoggerFactory.getLogger(AdvancedPriorityService.class);
    
    @Value("${huggingface.api.key:}")
    private String apiKey;
    
    private static final String HF_API_URL = "https://api-inference.huggingface.co/models/gpt2";
    
    @Override
    public int suggestPriority(String title, String description, String deadline) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("Hugging Face API key not configured");
        }
        
        try {
            String prompt = createPrompt(title, description, deadline);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(HF_API_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                    "{\"inputs\":\"" + prompt + "\",\"parameters\":{\"max_length\":100}}"))
                .build();
            
            HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                throw new RuntimeException("Hugging Face API error: " + response.body());
            }
            
            return parsePriorityResponse(response.body());
        } catch (Exception e) {
            logger.error("Failed to get Hugging Face suggestion", e);
            throw new RuntimeException("Failed to get AI suggestion: " + e.getMessage());
        }
    }
    
//    private String createPrompt(String title, String description, String deadline) {
//        return String.format(
//            "You are an expert task manager. Analyze this task and determine its priority (0-3) considering:\n" +
//            "1. Time needed (1=hours, 2=days, 3=weeks+)\n" +
//            "2. Deadline urgency (days until deadline: 0=today, 1=1-2 days, 2=3-7 days, 3=1+ weeks)\n" +
//            "3. Complexity (1=simple, 2=moderate, 3=complex)\n" +
//            "4. Impact (1=low, 2=medium, 3=high)\n\n" +
//            "Task: %s\nDescription: %s\nDeadline: %s\n\n" +
//            "Evaluate each factor (1-3), then calculate priority as:\n" +
//            "(Time + Urgency + Complexity + Impact) / 4, rounded to nearest integer\n\n" +
//            "Provide your analysis in this format:\n" +
//            "Time: [1-3] - [explanation]\n" +
//            "Urgency: [1-3] - [explanation]\n" +
//            "Complexity: [1-3] - [explanation]\n" +
//            "Impact: [1-3] - [explanation]\n" +
//            "Final Priority: [0-3]\n" +
//            "[number only on last line]",
//            title,
//            description,
//            deadline
//        );
//    }
    
    //enhanced prompt for better suggestions
    private String createPrompt(String title, String description, String deadline) {
        return String.format(
            "Analyze this development task and determine priority (0-3) considering:\n" +
            "1. TIME SENSITIVITY (days until deadline):\n" +
            "   - Overdue: 3\n" +
            "   - Today: 3\n" +
            "   - 1 day: 2.5\n" +
            "   - 2-3 days: 2\n" +
            "   - 4-7 days: 1\n" +
            "   - >7 days: 0.5\n\n" +
            "2. TASK COMPLEXITY:\n" +
            "   - Complete/build/create/implement: +2\n" +
            "   - Project/system/feature work: +1\n" +
            "   - Long description (>50 words): +1\n" +
            "   - Multiple requirements/steps: +1\n\n" +
            "3. URGENCY INDICATORS:\n" +
            "   - 'urgent', 'asap': +1\n" +
            "   - 'important', 'critical': +0.5\n\n" +
            "CALCULATION: (Time Sensitivity × 2) + Complexity + Urgency Indicators\n" +
            "PRIORITY: 0-1.9=None, 2-3.4=Low, 3.5-5.4=Medium, 5.5+=High\n\n" +
            "TASK: %s\nDESCRIPTION: %s\nDEADLINE: %s\n\n" +
            "Provide analysis in this format:\n" +
            "Time Sensitivity: [score]\n" +
            "Complexity: [score]\n" +
            "Urgency Indicators: [score]\n" +
            "Total: [score]\n" +
            "Final Priority: [0-3]",
            title,
            description,
            deadline
        );
    }
    
    
    private int parsePriorityResponse(String response) {
        try {
            // Get all lines
            String[] lines = response.split("\n");
            
            // Find the final priority line
            for (String line : lines) {
                if (line.startsWith("Final Priority:")) {
                    String[] parts = line.split(":");
                    return Integer.parseInt(parts[1].trim());
                }
            }
            
            // Fallback to last line if format changes
            String lastLine = lines[lines.length - 1].trim();
            return Integer.parseInt(lastLine);
        } catch (Exception e) {
            return 2; // Default to medium if parsing fails
        }
    }
}