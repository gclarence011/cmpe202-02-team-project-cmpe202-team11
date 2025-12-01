package com.backend.model;

import jakarta.persistence.*;

import java.time.*;

@Entity
@Table(name = "chats")
public class Chat {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String content;

  private LocalDateTime time;

  @ManyToOne
  @JoinColumn(name = "sender")
  private User sender;

  @ManyToOne
  @JoinColumn(name = "receiver")
  private User receiver;

  @ManyToOne
  @JoinColumn(name = "listing")
  private Listing listing;

  public Chat(String content, LocalDateTime time, User sender, User receiver, Listing listing){
    this.content = content;
    this.time = time;
    this.sender = sender;
    this.receiver = receiver;
    this.listing = listing;
  }
  
  public Chat(){
  }

  public Long getId() {return id;}

  public String getContent() {return content;}
  public void setContent(String content) {this.content = content;}

  public LocalDateTime getTime() {return time;}
  public void setTime(LocalDateTime time) {this.time = time;}

  public User getSender() {return sender;}
  public void setSender(User sender) {this.sender = sender;}

  public User getReceiver() {return receiver;}
  public void setReceiver(User receiver) {this.receiver = receiver;}

  public Listing getListing() {return listing;}
  public void setListing(Listing listing) {this.listing = listing;}
}
