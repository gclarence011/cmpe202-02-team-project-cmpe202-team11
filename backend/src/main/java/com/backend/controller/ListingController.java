package com.backend.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.service.ListingService;
import com.backend.service.UserService;
import com.backend.model.Listing;
import com.backend.model.Category;
import com.backend.dto.ListingsResult;
import com.backend.service.ImageService;

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
    String result = listingService.hello() + System.lineSeparator() + userService.hello();
    return result;
  }

  // Create Listing
  @PostMapping("api/listings/create")
  public ResponseEntity<?> createListing(){
    Long userId = 1L; // get from JWT
    try {
        Listing listing = listingService.createListing(userId);
        return ResponseEntity.status(201).body(listing);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("An unexpected error occurred" + e.getMessage());
    }
  }

  // Get Listing
    // Get list of listings from a search
  @GetMapping("api/listings/search")
  public ResponseEntity<?> getListings(@RequestParam String keyWord, Category category){
    Long userID = 1L; // from auth JWT
    try {
      ListingsResult listingsResult = listingService.searchListings(keyWord, category, userID);
      return ResponseEntity.status(200).body(listingsResult);
    } catch (IllegalArgumentException e) { // if no listing is found
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("An unexpected error occurred" + e.getMessage());
    }
  }
    // Get specific listing
  @GetMapping("api/listings/{listingId}")
  public ResponseEntity<?> getListing(@PathVariable Long listingId){
    Listing listing;
    try {
      listing = listingService.getListing(listingId);
      return ResponseEntity.status(200).body(listing);
    } catch (IllegalArgumentException e) { // if no listing is found
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("An unexpected error occurred" + e.getMessage());
    }
  }

  // Update Listing information (exluding image)
  @PatchMapping("api/listings/{listingId}") 
  public ResponseEntity<?> updateListing(
                                  @PathVariable Long listingId, 
                                  @RequestBody Map<String, Object> updates){
    Long userId = 1l; //get from JWT
    try {
      Listing listing = listingService.updateListing(listingId, updates, userId);
      return ResponseEntity.status(200).body(listing);
    } catch (IllegalArgumentException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("An unexpected error occured" + e.getMessage());
    }
  }
  // Upload Image
  @Autowired
  private ImageService s3Service; 

  @PostMapping("api/listings/{listingId}/upload")
  public ResponseEntity<?> upload(
                    @PathVariable Long listingId,
                    @RequestParam("file") MultipartFile image) throws IOException {
    Long userId = 1L; // get from JWT
    try{
      String url = listingService.uploadImage(image,listingId,userId);
      return ResponseEntity.status(200).body(url);
    } catch (IllegalArgumentException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("An unexpected error occured" + e.getMessage());
    }
  }

  // Flag Listings
  @PatchMapping("api/listings/{listingID}/flag")
  public ResponseEntity<?> flagListing(@PathVariable Long listingID){
    Long adminId = 1L; // get from JWT
    try{
      Listing listing = listingService.flagListing(listingID, adminId);
      return ResponseEntity.status(200).body(listing);
    } catch (IllegalArgumentException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e){
        return ResponseEntity.internalServerError().body("An unexpected error occured" + e.getMessage());
    }
  }

  // Delete Listing
  @DeleteMapping("api/listings/delete/{listingID}")
  public ResponseEntity<String> deleteListing(@PathVariable Long listingID){
    try{
      Long userId = 1l; // get rom JWT
      listingService.deleteListing(listingID, userId);
      return ResponseEntity.status(200).body("Deleted Succesfully");
    } catch (IllegalArgumentException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e){
        return ResponseEntity.internalServerError().body("An unexpected error occured" + e.getMessage());
    }
  }
  
}