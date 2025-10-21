package com.backend.dto;

import com.backend.model.Category;
import com.backend.model.Status;


public class ListingDTO {
  private Long id;

  private String title;
  private String description;
  private double price;
  private String[] photos;
  private Category category;
  private Long sellerId;
  private Status status;

  public ListingDTO() {
    this.title = "Untitled";
    this.status = Status.DRAFT;
  }
    
  //getters and setters
  public Long getId() {return id;}

  public String getTitle() {return title;}
  public void setTitle(String title) {this.title = title;}

  public String getDescription() {return description;}
  public void setDescription(String description) {this.description = description;}

  public double getPrice() {return price;}
  public void setPrice(double price) {this.price = price;}

  public String[] getPhotos() {return photos;}
  public void setPhotos(String[] photos) {this.photos = photos;}

  public Category getCategory() {return category;}
  public void setCategory(Category category) {this.category = category;}

  public Long getSeller() {return sellerId;}
  public void setSellerId(Long userId) {this.sellerId = userId;}

  public Status getStatus() {return status;}
  public void setStatus(Status status) {this.status = status;}

}

