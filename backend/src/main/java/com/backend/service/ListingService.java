package com.backend.service;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

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
  private final ImageService imageService;

  public ListingService(ListingRepository listingRepository, UserRepository userRepository, ImageService imageService){
    this.listingRepository = listingRepository;
    this.userRepository = userRepository;
    this.imageService = imageService;
  }

  public String hello() {
    return new String("Listing Serivces says hello!");
  }

  // Create listing
  public Listing createListing(Long userId){
    // Get the user creating the listing 
    System.out.println("find user");
    User seller = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException( "User not found"));
    System.out.println("user found");
    Listing listing = new Listing();
    System.out.println("Listing created");
    // Set the seller 
    listing.setSeller(seller);
    // Save into database
    try{
      listingRepository.save(listing);
      System.out.println("Creating listing" + listing.getTitle()); // comments
      return listing;
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
    }  catch (IllegalArgumentException e){
        throw e;
    }  catch (Exception e) {
        throw new RuntimeException("Database error: " + e.getMessage());
    }
    return listing;
  }
//----------------To Do --------------------------
    // batch search (to get list of listings) (TODO: Filter not implemented)
  public ListingsResult searchListings(String key, Category category){
    ListingsResult listingsResult = new ListingsResult(listingRepository.findAll());
    
    // query listing 
    // if empty
    return listingsResult;
  }

  public ListingsResult userListings(String userId){ // not implemented
    ListingsResult listingsResult = new ListingsResult(listingRepository.findAll());
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
    if (updates.containsKey("photos")){
      List<String> newPhotoList = (List<String>) updates.get("photos");
      List<String> currentPhotoList = listing.getPhotos();

      // Delete photos not in the new list
      Iterator<String> iterator = currentPhotoList.iterator();
      while (iterator.hasNext()) {
        String photoUrl = iterator.next();
        if (!newPhotoList.contains(photoUrl)){
          try {
            imageService.deleteImage(photoUrl);
            iterator.remove();
          } catch (Exception e){
          throw new RuntimeException("Error"+ e.getMessage(),e);
          }
        }
      }
      listing.setPhotos(currentPhotoList);
    }
    if (updates.containsKey("price")){
      try {
        listing.setPrice(Double.valueOf(updates.get("price").toString()));
      }catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid price format");
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

  // Upload photo
  public String uploadImage(MultipartFile image, Long listingId, Long userId){
    Listing listing = listingRepository.findById(listingId).orElseThrow(() -> new IllegalArgumentException("Listing not found"));
    User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    // Make sure that the user is the seller of the listing
    if (listing.getSeller() != user) { 
      throw new IllegalArgumentException("User is not the seller");
    }
    String imageURL;
    try{
      imageURL = imageService.uploadImage(image);
    }catch (IOException e){
      throw new RuntimeException("Failed to upload image: " + e.getMessage());
    }

    List<String> photos = listing.getPhotos();
    if (photos == null){
      photos = new ArrayList<>();
      listing.setPhotos(photos);
    }
    photos.add(imageURL);
    listing.setPhotos(photos);
    listingRepository.save(listing);
    return imageURL;
  }

  // Remove Listing (
  public void deleteListing(Long listingID, Long userId){
    Listing listing = listingRepository.findById(listingID).orElseThrow(() -> new RuntimeException("Listing not found"));
    User user = userRepository.findById(userId).orElse(null);
    if (listing.getSeller() != user) {
      throw new IllegalArgumentException("Only Listing Seller can delete");
    }
    List<String> photos = listing.getPhotos();
    // Delete image from s3
    if (photos != null && !photos.isEmpty()){
      for (String photoURL : photos){
        try{
          imageService.deleteImage(photoURL);
        } catch (Exception e){
          throw new RuntimeException("Error"+ e.getMessage(),e);
        }
      }
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
