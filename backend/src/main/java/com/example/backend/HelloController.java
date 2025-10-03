package com.example.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
  @GetMapping("/api/hello")
  public Message hello() {
    return new Message("Hello, World!");
  }

  static class Message {
    public String text;
    public Message(String text) {
      this.text = text;
    }
  }
}
