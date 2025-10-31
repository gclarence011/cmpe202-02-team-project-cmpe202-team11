package com.backend.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.backend.service.ImageService;

@RestController
public class HelloController {
  @GetMapping("")
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
