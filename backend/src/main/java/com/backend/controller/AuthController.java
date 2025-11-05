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
    public ResponseEntity<?> exchangeCode(@RequestBody Map<String, String> body) throws Exception {
        String code = body.get("code");
        return ResponseEntity.ok(cognitoService.exchangeCodeForTokens(code));
    }
}
  

