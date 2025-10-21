package com.backend.service;

import java.util.Map;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.backend.model.Listing;
import com.backend.model.Status;
import com.backend.model.User;
import com.backend.model.Category;
import com.backend.model.Role;

import com.backend.repository.ListingRepository;
import com.backend.repository.UserRepository;

import com.backend.dto.ListingsResult;

@Service
public class ListingService {
  // Initializiations 
  private final ListingRepository listingRepository;
  private final UserRepository userRepository;

  public ListingService(ListingRepository listingRepository, UserRepository userRepository){
    this.listingRepository = listingRepository;
    this.userRepository = userRepository;
  }

  public String hello() {
    return new String("Listing Serivces says hello!");
  }

  // Create listing
  public void createListing(Long userId){
    // Get the user creating the listing 
    User seller = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException( "User not found"));
    Listing listing = new Listing();
    // Set the seller 
    listing.setSeller(seller);
    // Save into database
    try{
      listingRepository.save(listing);
      System.out.println("Creating listing" + listing.getTitle()); // comments
    } catch (DataIntegrityViolationException e) {
        throw new IllegalArgumentException("Invalid listing data");
    } catch (Exception e) {
        throw new RuntimeException("Database error: " + e.getMessage());
    }
  }

  // Get Listing from ID
  public Listing getListing(Long id){
    Listing listing;
    try{
       listing = listingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException( "Listing not found"));
    }  catch (Exception e) {
        throw new RuntimeException("Database error: " + e.getMessage());
    }
    return listing;
  }
//----------------To Do --------------------------
    // batch search (to get list of listings) (TODO: Filter not implemented)
  public ListingsResult searchListings(String key, Category category, Long userId){
    ListingsResult listingsResult = new ListingsResult(listingRepository.findAll());
    
    // query listing 
    // if empty
    return listingsResult;
  }

  // update listing (Does not check if Photo URL is valid)
    // individual listing
  public Listing updateListing(Long listingId, Map<String, Object> updates, Long userId){
    Listing listing = listingRepository.findById(listingId).orElseThrow(() -> new IllegalArgumentException("Listing not found"));
    User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    // Make sure that the user is the seller of the listing
    if (listing.getSeller() != user) { 
      throw new IllegalArgumentException("User is not the seller");
    }
    // Update listing
    if (updates.containsKey("title")){
      listing.setTitle((String) updates.get("title"));
    }
    if (updates.containsKey("description")){
      listing.setDescription((String) updates.get("description"));
    }
    if (updates.containsKey("price")){
      try {
        listing.setPrice(Double.valueOf(updates.get("price").toString()));
      }catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid price format");
      }
    }
    if (updates.containsKey("photos")){
      Object photosObj = updates.get("photos");
      if (photosObj instanceof List<?>){
        List<?> rawlist = (List<?>) photosObj;
        List<String> photoList = rawlist.stream().map(Object::toString).toList();
        listing.setPhotos(photoList);
      } else {
        throw new IllegalArgumentException("Invalid photo format");
      }
    }
    if (updates.containsKey("category")){
      String categoryName = updates.get("category").toString();
      try {
        listing.setCategory(Category.valueOf(categoryName.toUpperCase()));
      }catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Invalid Catergory" + categoryName);
      }
    }
    if (updates.containsKey("status")){
      String status = updates.get("status").toString();
      try {
        listing.setStatus(Status.valueOf(status.toUpperCase()));
      }catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Invalid Status" + status);
      }
    }
    return listingRepository.save(listing);
  }

  // Remove Listing
  public void deleteListing(Long listingID, Long userId){
    Listing listing = listingRepository.findById(listingID).orElseThrow(() -> new RuntimeException("Listing not found"));
    User user = userRepository.findById(userId).orElse(null);
    if (listing.getSeller() != user) {
      throw new IllegalArgumentException("Only Listing Seller can delete");
    }
    listingRepository.delete(listing);
  }

  // Flag listing
  public Listing flagListing(Long listingId, Long adminId){
    Listing listing = listingRepository.findById(listingId).orElseThrow(() -> new RuntimeException("Listing not found"));
    User admin = userRepository.findById(adminId).orElse(null);
    if (admin.getRole() != Role.ADMIN){
      throw new IllegalArgumentException("Only Admin can flag");
    }
    listing.setStatus(Status.FLAGED);

    return listingRepository.save(listing);
  }
  
}
