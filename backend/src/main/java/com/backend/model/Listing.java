package com.backend.model;

import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "listings")
public class Listing {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;
  private String description;
  @PositiveOrZero(message = "Price must be positive")
  private double price;
  private List<String> photos = new ArrayList<>();
  @Enumerated(EnumType.STRING)
  private Category category;
  @ManyToOne
  @JoinColumn(name = "sellerid")
  private User seller;
  @Enumerated(EnumType.STRING)
  private Status status;

  public Listing(String title, String description, double price, List<String> photos, Category category, User seller) {
    this.title = title;
    this.description = description;
    this.price = price;
    this.photos = photos;
    this.category = category;
    this.seller = seller;
    this.status = Status.DRAFT;
  }
  public Listing() {
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

  public List<String> getPhotos() {return photos;}
  public void setPhotos(List<String> photos) {this.photos = photos;}

  public Category getCategory() {return category;}
  public void setCategory(Category category) {this.category = category;}

  public User getSeller() {return seller;}
  public void setSeller(User seller) {this.seller = seller;}

  public Status getStatus() {return status;}
  public void setStatus(Status status) {this.status = status;}

}
