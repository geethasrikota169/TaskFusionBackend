package com.klef.fsd.sdp.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class RuleBasedPriorityService implements PriorityService {
    
    @Override
    public int suggestPriority(String title, String description, String deadline) {
        int score = 0;
        
        // 1. Time Sensitivity Analysis (40% weight)
        if (deadline != null && !deadline.isEmpty()) {
            try {
                LocalDate dueDate = LocalDate.parse(deadline);
                long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
                
                if (daysUntil < 0) score += 4;      // Overdue
                else if (daysUntil == 0) score += 4; // Due today
                else if (daysUntil <= 1) score += 3; // Due tomorrow
                else if (daysUntil <= 3) score += 2; // Due in 2-3 days
                else if (daysUntil <= 7) score += 1; // Due in a week
            } catch (Exception e) {
                // Date parsing failed
            }
        }
        
        // 2. Task Complexity Analysis (40% weight)
        int complexityScore = assessComplexity(title, description);
        score += complexityScore;
        
        // 3. Keyword Analysis (20% weight)
        String text = (title + " " + description).toLowerCase();
        if (text.contains("urgent") || text.contains("asap")) score += 2;
        if (text.contains("important") || text.contains("critical")) score += 1;
        
        // Normalize to priority scale
        if (score >= 6) return 3; // High
        if (score >= 4) return 2; // Medium
        if (score >= 2) return 1; // Low
        return 0; // None
    }
    
    private int assessComplexity(String title, String description) {
        int complexity = 0;
        
        // 1. Task Type Analysis
        String lowerTitle = title.toLowerCase();
        if (lowerTitle.contains("complete") || lowerTitle.contains("build") || 
            lowerTitle.contains("create") || lowerTitle.contains("implement")) {
            complexity += 2;
        }
        
        // 2. Scope Indicators
        if (lowerTitle.contains("project") || lowerTitle.contains("system") || 
            lowerTitle.contains("feature")) {
            complexity += 1;
        }
        
        // 3. Description Analysis
        if (description != null) {
            int wordCount = description.split("\\s+").length;
            if (wordCount > 50) complexity += 1;
            if (description.contains(";") || description.contains("\n")) complexity += 1;
        }
        
        return complexity;
    }
}