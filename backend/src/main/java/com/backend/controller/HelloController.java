package com.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
  @GetMapping("/health")
  public Message hello() {
    return new Message("Backend is running");
  }

  static class Message {
    public String text;
    public Message(String text) {
      this.text = text;
    }
  }

  
}
