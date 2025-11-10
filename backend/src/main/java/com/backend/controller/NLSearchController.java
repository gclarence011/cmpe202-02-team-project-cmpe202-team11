package com.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backend.dto.NLPSearchRequest;
import com.backend.dto.ListingsResult;
import com.backend.service.NLPSearchService;

@RestController
public class NLSearchController {
    
    private static final Logger logger = LoggerFactory.getLogger(NLSearchController.class);
    private final NLPSearchService nlpSearchService;
    
    public NLSearchController(NLPSearchService nlpSearchService) {
        this.nlpSearchService = nlpSearchService;
    }
    
    /**
     * Process natural language search query
     * Accepts a query like "CMPE 202 textbook under $50"
     * and returns matching listings
     * 
     * @param request NLPSearchRequest containing the query and optional search history
     * @return ResponseEntity containing ListingsResult with matching listings
     */
    @PostMapping("api/search/nlp")
    public ResponseEntity<?> searchNLP(@RequestBody NLPSearchRequest request) {
        try {
            logger.info("Received NLP search request with query: {}", request.getQuery());
            
            // Validate input
            if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Query cannot be empty");
            }
            
            // Process the NLP search
            ListingsResult results = nlpSearchService.processNLPSearch(request);
            
            logger.info("NLP search completed. Found {} listings", results.getListings().size());
            
            return ResponseEntity.ok(results);
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid NLP search request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing NLP search: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }
    
    /**
     * Health check endpoint to verify search service is running
     */
    @GetMapping("api/search/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok().body("{\"status\": \"NLP Search Service is running\"}");
    }
}
