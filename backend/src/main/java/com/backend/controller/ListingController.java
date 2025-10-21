package com.backend.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.service.ListingService;
import com.backend.service.UserService;
import com.backend.model.Listing;
import com.backend.model.Category;
import com.backend.dto.ListingsResult;

@RestController
public class ListingController {
  private final ListingService listingService;
  private final UserService userService;


  public ListingController(UserService userService, ListingService listingService){
    this.userService = userService;
    this.listingService = listingService;
  }
  
  @GetMapping("api/listings/hello")// for testing 
  public String hello(){
    String result = listingService.hello() + System.lineSeparator() + userService.hello(); // Need to fix)
    return result;
  }

  // Create Listing
  @PostMapping("api/listings/create")
  public ResponseEntity<String> createListing(@RequestBody Listing listing){
    Long userId = 1L; // get from JWT
    try {
        listingService.createListing(listing, userId);
        return ResponseEntity.status(201).body("Listing created successfully");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("An unexpected error occurred");
    }
  }

  // Get Listing
  @GetMapping("api/listings/search")
  public ResponseEntity<?> getListings(@RequestParam String keyWord, Category category){
    Long userID = 1L; // from auth JWT
    try {
      ListingsResult listingsResult = listingService.searchListings(keyWord, category, userID);
      return ResponseEntity.status(201).body(listingsResult);
    } catch (IllegalArgumentException e) { // if no listing is found
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("An unexpected error occurred");
    }
  }

  @GetMapping("api/listings/{listingId}")
  public ResponseEntity<?> getListing(@PathVariable Long listingId){
    Listing listing;
    try {
      listing = listingService.getListing(listingId);
      return ResponseEntity.status(201).body(listing);
    } catch (IllegalArgumentException e) { // if no listing is found
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("An unexpected error occurred");
    }
  }

  // Update Listing
  @PatchMapping("api/listings/{listingId}") 
  public ResponseEntity<Listing> updateListing(
                                  @PathVariable Long listingId, 
                                  @RequestBody Map<String, Object> updates){
    Long userId = 1l; //get from JWT
    Listing listing = listingService.updateListing(listingId, updates, userId);
    return ResponseEntity.ok(listing);
  }

  @PatchMapping("api/listings/{id}/flag")
  public ResponseEntity<Listing> flagListing(Long listingID){
    Long adminId = 1L; // get from JWT
    Listing listing = listingService.flagListing(listingID, adminId);
    return ResponseEntity.ok(listing);
  }

  // Delete Listing
  @DeleteMapping("api/listings/delete/:id")
  public void deleteListing(Long listingID){
    Long userId = 1l; // get rom JWT
    listingService.deleteListing(listingID, userId);
  }
  
}