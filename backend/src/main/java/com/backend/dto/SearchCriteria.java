package com.backend.dto;

public class SearchCriteria {
    private String keyword;
    private Double minPrice;
    private Double maxPrice;
    private String category;
    
    public SearchCriteria() {}
    
    public SearchCriteria(String keyword, Double minPrice, Double maxPrice, String category) {
        this.keyword = keyword;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.category = category;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public Double getMinPrice() {
        return minPrice;
    }
    
    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }
    
    public Double getMaxPrice() {
        return maxPrice;
    }
    
    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    @Override
    public String toString() {
        return "SearchCriteria{" +
                "keyword='" + keyword + '\'' +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", category='" + category + '\'' +
                '}';
    }
}
