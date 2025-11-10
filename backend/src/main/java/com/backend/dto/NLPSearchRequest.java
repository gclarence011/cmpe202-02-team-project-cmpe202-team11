package com.backend.dto;

import java.util.List;

public class NLPSearchRequest {
    private String query;
    private List<String> searchHistory;  // Previous search queries for context
    
    public NLPSearchRequest() {}
    
    public NLPSearchRequest(String query) {
        this.query = query;
        this.searchHistory = List.of();
    }
    
    public NLPSearchRequest(String query, List<String> searchHistory) {
        this.query = query;
        this.searchHistory = searchHistory;
    }
    
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public List<String> getSearchHistory() {
        return searchHistory;
    }
    
    public void setSearchHistory(List<String> searchHistory) {
        this.searchHistory = searchHistory;
    }
}
