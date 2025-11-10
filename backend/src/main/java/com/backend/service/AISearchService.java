package com.backend.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backend.dto.SearchCriteria;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * AI-powered search service using Google Gemini API
 * Understands natural language queries without hardcoded rules
 */
@Service
public class AISearchService {
    
    private static final Logger logger = LoggerFactory.getLogger(AISearchService.class);
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    
    @Value("${google.api.key}")
    private String googleApiKey;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    
    /**
     * Use AI to parse natural language search query and extract structured criteria
     * Handles: "under 30", "laptop under 100", "textbooks between 20 and 50", etc.
     */
    public SearchCriteria parseSearchWithAI(String query) {
        try {
            logger.info("Parsing query with AI: {}", query);
            
            // Create prompt for Gemini to understand the search intent
            String prompt = buildSearchPrompt(query);
            
            String aiResponse = callGeminiAPI(prompt);
            logger.info("AI Response: {}", aiResponse);
            
            SearchCriteria criteria = parseAIResponse(aiResponse, query);
            logger.info("Extracted criteria: keyword={}, minPrice={}, maxPrice={}, category={}", 
                criteria.getKeyword(), criteria.getMinPrice(), criteria.getMaxPrice(), criteria.getCategory());
            
            return criteria;
        } catch (Exception e) {
            logger.error("Error parsing search with AI: {}", e.getMessage(), e);
            // Fallback to basic parsing
            return parseSearchFallback(query);
        }
    }
    
    /**
     * Build a prompt that tells Gemini how to parse the search
     */
    private String buildSearchPrompt(String query) {
        return """
            Extract search criteria from this user query. Return ONLY valid JSON (no markdown, no code blocks).
            
            User query: "%s"
            
            Extract:
            - keyword: main product/item being searched (null if not specified)
            - minPrice: minimum price in dollars (null if not specified)
            - maxPrice: maximum price in dollars (null if not specified)
            - category: product category (null if not specified, options: BOOKS, ELECTRONICS, CLOTHING, FURNITURE, SPORTS, OTHER)
            
            Examples:
            "under 30" → {"keyword": null, "minPrice": null, "maxPrice": 30, "category": null}
            "laptop under 100" → {"keyword": "laptop", "minPrice": null, "maxPrice": 100, "category": null}
            "textbooks 20 to 50" → {"keyword": "textbooks", "minPrice": 20, "maxPrice": 50, "category": null}
            "cheap electronics" → {"keyword": null, "minPrice": null, "maxPrice": null, "category": "ELECTRONICS"}
            
            Return only the JSON object, nothing else."""
                .formatted(query);
    }
    
    /**
     * Call Google Gemini API to parse the search query
     */
    private String callGeminiAPI(String prompt) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, String> part = new HashMap<>();
        
        part.put("text", prompt);
        content.put("parts", new Object[]{part});
        requestBody.put("contents", new Object[]{content});
        
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(GEMINI_API_URL + "?key=" + googleApiKey))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            logger.error("Gemini API error: {} - {}", response.statusCode(), response.body());
            throw new RuntimeException("Gemini API failed: " + response.statusCode());
        }
        
        // Extract text from Gemini response
        String responseBody = response.body();
        logger.debug("Raw Gemini response: {}", responseBody);
        
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
        java.util.List<?> contents = (java.util.List<?>) responseMap.get("candidates");
        if (contents == null || contents.isEmpty()) {
            throw new RuntimeException("No candidates in Gemini response");
        }
        
        Map<String, Object> candidate = (Map<String, Object>) contents.get(0);
        Map<String, Object> contentMap = (Map<String, Object>) candidate.get("content");
        java.util.List<?> parts = (java.util.List<?>) contentMap.get("parts");
        if (parts == null || parts.isEmpty()) {
            throw new RuntimeException("No parts in Gemini response");
        }
        
        Map<String, Object> textPart = (Map<String, Object>) parts.get(0);
        return (String) textPart.get("text");
    }
    
    /**
     * Parse the AI response and extract SearchCriteria
     */
    private SearchCriteria parseAIResponse(String aiResponse, String originalQuery) {
        try {
            // Clean the response (Gemini might return markdown)
            String cleanedResponse = aiResponse
                .replace("```json", "")
                .replace("```", "")
                .trim();
            
            Map<String, Object> parsed = objectMapper.readValue(cleanedResponse, Map.class);
            
            SearchCriteria criteria = new SearchCriteria();
            
            // Extract keyword
            Object keyword = parsed.get("keyword");
            if (keyword != null && !keyword.toString().equalsIgnoreCase("null")) {
                criteria.setKeyword(keyword.toString());
            }
            
            // Extract prices
            Object minPrice = parsed.get("minPrice");
            if (minPrice != null && !minPrice.toString().equalsIgnoreCase("null")) {
                try {
                    criteria.setMinPrice(Double.parseDouble(minPrice.toString()));
                } catch (NumberFormatException e) {
                    logger.warn("Could not parse minPrice: {}", minPrice);
                }
            }
            
            Object maxPrice = parsed.get("maxPrice");
            if (maxPrice != null && !maxPrice.toString().equalsIgnoreCase("null")) {
                try {
                    criteria.setMaxPrice(Double.parseDouble(maxPrice.toString()));
                } catch (NumberFormatException e) {
                    logger.warn("Could not parse maxPrice: {}", maxPrice);
                }
            }
            
            // Extract category
            Object category = parsed.get("category");
            if (category != null && !category.toString().equalsIgnoreCase("null")) {
                criteria.setCategory(category.toString());
            }
            
            // Set defaults if not set
            if (criteria.getMinPrice() == null) {
                criteria.setMinPrice(0.0);
            }
            if (criteria.getMaxPrice() == null) {
                criteria.setMaxPrice(Double.MAX_VALUE);
            }
            
            return criteria;
        } catch (Exception e) {
            logger.error("Error parsing AI response: {}", aiResponse, e);
            return parseSearchFallback(originalQuery);
        }
    }
    
    /**
     * Fallback parsing if AI fails (basic regex patterns)
     */
    private SearchCriteria parseSearchFallback(String query) {
        SearchCriteria criteria = new SearchCriteria();
        String lowerQuery = query.toLowerCase();
        
        // Try to extract price
        java.util.regex.Pattern pricePattern = java.util.regex.Pattern.compile("(\\d+(?:\\.\\d{2})?)");
        java.util.regex.Matcher priceMatcher = pricePattern.matcher(lowerQuery);
        
        if (lowerQuery.contains("under") || lowerQuery.contains("below") || lowerQuery.contains("less than")) {
            if (priceMatcher.find()) {
                criteria.setMaxPrice(Double.parseDouble(priceMatcher.group(1)));
            }
        } else if (lowerQuery.contains("over") || lowerQuery.contains("more than") || lowerQuery.contains("above")) {
            if (priceMatcher.find()) {
                criteria.setMinPrice(Double.parseDouble(priceMatcher.group(1)));
            }
        }
        
        // Set defaults
        if (criteria.getMinPrice() == null) {
            criteria.setMinPrice(0.0);
        }
        if (criteria.getMaxPrice() == null) {
            criteria.setMaxPrice(Double.MAX_VALUE);
        }
        
        // Store remaining text as keyword
        String cleanedQuery = lowerQuery
            .replaceAll("(?:under|over|below|above|less than|more than)\\s*\\d+", "")
            .trim();
        if (!cleanedQuery.isEmpty()) {
            criteria.setKeyword(cleanedQuery);
        }
        
        return criteria;
    }
}
