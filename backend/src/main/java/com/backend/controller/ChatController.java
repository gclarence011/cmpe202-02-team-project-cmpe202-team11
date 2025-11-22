package com.backend.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;

import com.backend.service.ChatService;
import com.backend.service.UserService;
import com.backend.model.Listing;
import com.backend.model.Chat;

@RestController
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;

    public ChatController(UserService userService, ChatService chatService){
        this.userService = userService;
        this.chatService = chatService;
    }

    @GetMapping("chats")
    public ResponseEntity<?> getChats(){
        System.out.println("test1");
        return ResponseEntity.status(201).body("test2");
    }

    @PostMapping("chats")
    public ResponseEntity<?> createChat(@RequestParam Long receiverId, Long listingId, String content, @AuthenticationPrincipal Jwt jwt){
        String CogId =  jwt.getClaim("sub");
        Long senderId = userService.getUserId(CogId);
        try {
            Chat chat = chatService.createChat(senderId, receiverId, listingId, content);
            return ResponseEntity.status(201).body(chat);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred" + e.getMessage());
        }
    }
}