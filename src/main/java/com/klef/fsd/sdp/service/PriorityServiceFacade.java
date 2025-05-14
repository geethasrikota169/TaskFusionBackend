package com.klef.fsd.sdp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class PriorityServiceFacade implements PriorityService {
    
    private final RuleBasedPriorityService ruleBasedService;
    private final AdvancedPriorityService advancedService;
    
    @Autowired
    public PriorityServiceFacade(
        RuleBasedPriorityService ruleBasedService,
        AdvancedPriorityService advancedService
    ) {
        this.ruleBasedService = ruleBasedService;
        this.advancedService = advancedService;
    }
    
    @Override
    public int suggestPriority(String title, String description, String deadline) {
        try {
            // First try the advanced service
            return advancedService.suggestPriority(title, description, deadline);
        } catch (Exception e) {
            // Fall back to rule-based if advanced fails
            return ruleBasedService.suggestPriority(title, description, deadline);
        }
    }
}