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
  private final ListingRepository listingRepository;
  private final UserRepository userRepository;

  public ListingService(ListingRepository listingRepository, UserRepository userRepository){
    this.listingRepository = listingRepository;
    this.userRepository = userRepository;
  }

  public String hello() {
    return new String("Listing Serivces says hello!");
  }

  // create listing
  public void createListing(Listing listing, Long userId){
    // Checks if listing is not empty
    if (listing == null){
      throw new IllegalArgumentException ("Listing cannot be empty");
    }
    // Get the user creating the listing 
    User seller = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException( "User not found"));
    // Set the seller and save into database
    listing.setSeller(seller);
    try{
      listingRepository.save(listing);
      System.out.println("Creating listing" + listing.getTitle()); // comments
    } catch (DataIntegrityViolationException e) {
        throw new IllegalArgumentException("Invalid listing data");
    } catch (Exception e) {
        throw new RuntimeException("Database error: " + e.getMessage());
    }
  }

  // read listing
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

  // update listing (Check URL????)
    // individual listing
  public Listing updateListing(Long listingId, Map<String, Object> updates, Long userId){
    Listing listing = listingRepository.findById(listingId).orElseThrow(() -> new RuntimeException("Listing not found"));
    
    // update
    if (updates.containsKey("title")){
      listing.setTitle((String) updates.get("title"));

      System.err.println("title change");
    }
    if (updates.containsKey("description")){
      System.err.println("description change");
      listing.setDescription((String) updates.get("description"));
    }
    if (updates.containsKey("price")){
      try {
        listing.setPrice(Double.valueOf(updates.get("price").toString()));
      }catch (NumberFormatException e) {
        throw new RuntimeException("Invalid price format");
      }
    }
    if (updates.containsKey("photos")){
      Object photosObj = updates.get("photos");
      if (photosObj instanceof List<?>){
        List<?> rawlist = (List<?>) photosObj;
        List<String> photoList = rawlist.stream().map(Object::toString).toList();
        listing.setPhotos(photoList);
      } else {
        throw new RuntimeException("Invalid photo format");
      }
    }
    if (updates.containsKey("category")){
      String categoryName = updates.get("category").toString();
      try {
        listing.setCategory(Category.valueOf(categoryName.toUpperCase()));
      }catch (IllegalArgumentException e) {
        throw new RuntimeException("Invalid Catergory" + categoryName);
      }
    }
    if (updates.containsKey("status")){
      String status = updates.get("status").toString();
      try {
        listing.setStatus(Status.valueOf(status.toUpperCase()));
      }catch (IllegalArgumentException e) {
        throw new RuntimeException("Invalid Status" + status);
      }
    }
    return listingRepository.save(listing);
  }

  // remove listing
    // individual listing
  public void deleteListing(Long listingID, Long userId){
    Listing listing = listingRepository.findById(listingID).orElseThrow(() -> new RuntimeException("Listing not found"));
    User user = userRepository.findById(userId).orElse(null);
    if (listing.getSeller() != user) {
      throw new RuntimeException(" Listing Seller can flag");
    }
    listingRepository.delete(listing);
  }

  // flag listing
  public Listing flagListing(Long listingId, Long adminId){
    Listing listing = listingRepository.findById(listingId).orElseThrow(() -> new RuntimeException("Listing not found"));
    User admin = userRepository.findById(adminId).orElse(null);
    if (admin.getRole() != Role.ADMIN){
      throw new RuntimeException("Only Admin can flag");
    }
    listing.setStatus(Status.FLAGED);

    return listingRepository.save(listing);
  }
  
}
