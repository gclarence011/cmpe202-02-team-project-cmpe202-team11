package com.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
  @GetMapping("/health")
  public ResponseEntity<?>  hello() {
    return ResponseEntity.ok().body("Backend is running");
  }
}

  

