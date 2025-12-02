package com.backend.service;

import org.springframework.stereotype.Service;

import com.backend.repository.UserRepository;
import com.backend.model.Role;
import com.backend.model.User;

@Service
public class UserService {
  // test if service works
  // Initializiations 
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository){
    this.userRepository = userRepository;
  }

  public String hello() {
    return new String("User Serivces says hello!");
  }

  public Long getUserId(String CogId){
    User user = userRepository.findByCogId(CogId);
    return user.getID();
  }

  public boolean isAdmin(String CogId){
    User user = userRepository.findByCogId(CogId);
    return user.getRole() == Role.ADMIN;
  }
  
}
