package com.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import com.backend.security.CognitoService;;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private CognitoService cognitoService;

    @PostMapping("/exchange")
    public ResponseEntity<?> exchangeCode(@RequestBody Map<String, String> body) {
        try {
            System.out.println("exchanecode started");
            String code = body.get("code");

            // Call the Cognito service to exchange code for tokens
            Map<String, Object> tokens = cognitoService.exchangeCodeForTokens(code);
            return ResponseEntity.ok(tokens);

        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error exchanging code: " + e.getMessage());
            e.printStackTrace();

            // Return an error response with HTTP 500
            return ResponseEntity
                    .status(500)
                    .body(Map.of("error", "Failed to exchange code", "details", e.getMessage()));
        }
    }
}
  

