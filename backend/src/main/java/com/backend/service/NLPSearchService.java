package com.backend.service;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backend.dto.SearchCriteria;
import com.backend.dto.NLPSearchRequest;
import com.backend.dto.ListingsResult;
import com.backend.model.Listing;
import com.backend.model.Category;
import com.backend.repository.ListingRepository;

@Service
public class NLPSearchService {
    
    private static final Logger logger = LoggerFactory.getLogger(NLPSearchService.class);
    
    @Value("${google.api.key}")
    private String googleApiKey;
    
    private final ListingRepository listingRepository;
    private final AISearchService aiSearchService;
    
    public NLPSearchService(ListingRepository listingRepository, AISearchService aiSearchService) {
        this.listingRepository = listingRepository;
        this.aiSearchService = aiSearchService;
    }
    
    /**
     * Process natural language search query and return matching listings
     * 
     * @param request NLPSearchRequest containing query and search history
     * @return ListingsResult containing matching listings
     */
    public ListingsResult processNLPSearch(NLPSearchRequest request) {
        try {
            // Combine search history with current query for better context
            String combinedQuery = buildContextualQuery(request);
            
            logger.info("Processing NLP search with query: {}", combinedQuery);
            
            // Parse natural language query to extract search criteria
            SearchCriteria criteria = parseNaturalLanguageQuery(combinedQuery);
            
            logger.info("Extracted search criteria: {}", criteria);
            
            // Execute search based on criteria
            List<Listing> results = executeDatabaseSearch(criteria);
            
            logger.info("Found {} listings matching criteria", results.size());
            
            return new ListingsResult(results);
            
        } catch (Exception e) {
            logger.error("Error processing NLP search: {}", e.getMessage(), e);
            return new ListingsResult(new ArrayList<>());
        }
    }
    
    /**
     * Build a contextual query by combining search history with current query
     */
    private String buildContextualQuery(NLPSearchRequest request) {
        StringBuilder contextualQuery = new StringBuilder(request.getQuery());
        
        // If there's search history, combine them to understand refinements
        if (request.getSearchHistory() != null && !request.getSearchHistory().isEmpty()) {
            // Example: "textbook for CMPE 202" + "less than $50" becomes combined context
            contextualQuery.append(" ").append(String.join(" ", request.getSearchHistory()));
        }
        
        return contextualQuery.toString();
    }
    
    /**
     * Parse natural language query to extract structured search criteria
     * Now uses AI (Gemini) instead of hardcoded rules
     */
    private SearchCriteria parseNaturalLanguageQuery(String query) {
        // Use AI to understand the query
        return aiSearchService.parseSearchWithAI(query);
    }
    
    /**
     * Execute database search based on extracted criteria
     * Also applies fuzzy matching for typo tolerance in keywords
     */
    private List<Listing> executeDatabaseSearch(SearchCriteria criteria) {
        List<Listing> results = new ArrayList<>();
        
        boolean hasKeyword = criteria.getKeyword() != null && !criteria.getKeyword().isEmpty();
        boolean hasCategory = criteria.getCategory() != null && !criteria.getCategory().isEmpty();
        boolean hasPrice = criteria.getMinPrice() != null || criteria.getMaxPrice() != null;
        
        if (hasKeyword && hasCategory && hasPrice) {
            // All three: keyword, category, and price
            Category category = Category.valueOf(criteria.getCategory());
            results = listingRepository.searchByCriteria(
                criteria.getKeyword(),
                criteria.getMinPrice(),
                criteria.getMaxPrice(),
                category
            );
        } else if (hasKeyword && hasCategory) {
            // Keyword and category
            Category category = Category.valueOf(criteria.getCategory());
            results = listingRepository.searchByKeywordAndCategory(criteria.getKeyword(), category);
            results = filterByPrice(results, criteria.getMinPrice(), criteria.getMaxPrice());
        } else if (hasKeyword && hasPrice) {
            // Keyword and price
            results = listingRepository.searchByKeywordAndPrice(
                criteria.getKeyword(),
                criteria.getMinPrice(),
                criteria.getMaxPrice()
            );
        } else if (hasCategory && hasPrice) {
            // Category and price
            Category category = Category.valueOf(criteria.getCategory());
            results = listingRepository.searchByCategory(category);
            results = filterByKeywordAndPrice(results, "", criteria.getMinPrice(), criteria.getMaxPrice());
        } else if (hasKeyword) {
            // Keyword only - use fuzzy matching for typo tolerance
            results = listingRepository.searchByKeyword(criteria.getKeyword());
            
            // If no exact matches, try fuzzy matching
            if (results.isEmpty()) {
                results = fuzzySearchListings(criteria.getKeyword());
            }
        } else if (hasCategory) {
            // Category only
            Category category = Category.valueOf(criteria.getCategory());
            results = listingRepository.searchByCategory(category);
        } else if (hasPrice) {
            // Price only
            results = listingRepository.searchByPriceRange(criteria.getMinPrice(), criteria.getMaxPrice());
        } else {
            // No criteria - return all active listings
            results = listingRepository.findAllActive();
        }
        
        return results;
    }
    
    /**
     * Filter results by price range
     */
    private List<Listing> filterByPrice(List<Listing> listings, Double minPrice, Double maxPrice) {
        List<Listing> filtered = new ArrayList<>();
        for (Listing listing : listings) {
            if (listing.getPrice() >= minPrice && listing.getPrice() <= maxPrice) {
                filtered.add(listing);
            }
        }
        return filtered;
    }
    
    /**
     * Filter results by keyword and price
     */
    private List<Listing> filterByKeywordAndPrice(List<Listing> listings, String keyword, Double minPrice, Double maxPrice) {
        List<Listing> filtered = new ArrayList<>();
        for (Listing listing : listings) {
            boolean priceMatch = listing.getPrice() >= minPrice && listing.getPrice() <= maxPrice;
            boolean keywordMatch = keyword.isEmpty() || 
                                 listing.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                                 listing.getDescription().toLowerCase().contains(keyword.toLowerCase());
            
            if (priceMatch && keywordMatch) {
                filtered.add(listing);
            }
        }
        return filtered;
    }

    /**
     * Calculate Levenshtein distance (edit distance) between two strings
     * Used for fuzzy matching and typo tolerance
     * Returns a score from 0-100 where 100 is a perfect match
     */
    private int levenshteinDistance(String s1, String s2) {
        String a = s1.toLowerCase();
        String b = s2.toLowerCase();
        
        if (a.isEmpty()) return b.length();
        if (b.isEmpty()) return a.length();
        
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(
                    dp[i - 1][j] + 1,      // deletion
                    dp[i][j - 1] + 1),    // insertion
                    dp[i - 1][j - 1] + cost  // substitution
                );
            }
        }
        
        return dp[a.length()][b.length()];
    }

    /**
     * Calculate similarity score between two strings (0-100)
     * Used to find fuzzy matches for typos and partial matches
     */
    private int similarityScore(String s1, String s2) {
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 100;
        
        int distance = levenshteinDistance(s1, s2);
        return 100 - (distance * 100 / maxLen);
    }

    /**
     * Perform fuzzy search on all active listings
     * Matches keywords even with typos or partial matches
     */
    private List<Listing> fuzzySearchListings(String keyword) {
        List<Listing> allListings = listingRepository.findAllActive();
        List<Listing> fuzzyMatches = new ArrayList<>();
        
        String lowerKeyword = keyword.toLowerCase();
        int minWordSimilarity = 35;  // 50% match threshold for better typo tolerance
        
        // Split keyword into words and search for each word
        String[] queryWords = lowerKeyword.split("\\s+");
        
        for (Listing listing : allListings) {
            String title = listing.getTitle().toLowerCase();
            String description = listing.getDescription() != null ? listing.getDescription().toLowerCase() : "";
            String combined = title + " " + description;
            
            // Check if any query word matches (fuzzy or exact)
            boolean matches = false;
            for (String queryWord : queryWords) {
                if (queryWord.length() >= 2) {  // Only match words with 2+ chars
                    // Split title and description into words
                    String[] titleWords = combined.split("\\s+|[\\p{Punct}]+");
                    
                    for (String titleWord : titleWords) {
                        if (titleWord.length() >= 2) {
                            int similarity = similarityScore(queryWord, titleWord);
                            // Also check if query word is contained in title word (prefix matching)
                            boolean isPrefix = titleWord.startsWith(queryWord) || queryWord.startsWith(titleWord.substring(0, Math.min(2, titleWord.length())));
                            
                            if (similarity >= minWordSimilarity || isPrefix) {
                                matches = true;
                                break;
                            }
                        }
                    }
                    
                    if (matches) break;
                }
            }
            
            if (matches && !fuzzyMatches.contains(listing)) {
                fuzzyMatches.add(listing);
            }
        }
        
        return fuzzyMatches;
    }
}
