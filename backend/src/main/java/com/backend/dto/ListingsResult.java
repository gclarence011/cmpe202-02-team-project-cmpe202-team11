package com.backend.dto;

import java.util.List;
import java.util.ArrayList;
import com.backend.model.Listing;


public class ListingsResult {
  private List<Listing> listings = new ArrayList<>();

  public ListingsResult(){}

  public ListingsResult(List<Listing> listings){
    this.listings  = listings;
  }

  public List<Listing> getListings(){ return listings;}
  public void setListings(List<Listing> listings) { this.listings = listings; }

  public void addListing(Listing listing) {listings.add(listing);}

}

