package com.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String email;
  @Enumerated(EnumType.STRING)
  private Role role;

  public User(String name, String email){
    this.name = name;
    this.email = email;
    this.role = Role.USER;
  }

  public User(){
    this.name = "Unknown";
    this.role = Role.USER;
  }

  // setters and getters
  public Long getID() {return id;}

  public String getName() {return name;}
  public void setName(String name) {this.name = name;}

  public String getEmail() {return email;}
  public void setEmail(String email) {this.email = email;}

  public Role getRole() {return role;}
  public void setRole(Role role) {this.role = role;}
}
