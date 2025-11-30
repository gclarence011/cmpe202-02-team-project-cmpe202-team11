package com.backend.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        System.out.println(chatService.getChats());
        return ResponseEntity.status(201).body(chatService.getChats());
    }

    @PostMapping("chats")
    public ResponseEntity<?> createChat(@RequestBody Map<String, Object> body, @AuthenticationPrincipal Jwt jwt){
        System.out.println("POST1");

        String CogId =  jwt.getClaim("sub");
        Long senderId = userService.getUserId(CogId);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode request = objectMapper.readTree(body.get("body").toString());
            System.out.println(body.get("body").toString());

            String receiverId = request.get("receiverId").toString();
            String listingId = request.get("listingId").toString();
            String message = request.get("message").toString();
            if (receiverId.startsWith("\"")) {
                receiverId = receiverId.substring(1, receiverId.length()-1);
                listingId = listingId.substring(1, listingId.length()-1);
            }
            message = message.substring(1, message.length()-1);

            System.out.println(
                senderId + " " + 
                receiverId + " " + 
                listingId+ " " + 
                message);
            Chat chat = chatService.createChat(
                senderId, 
                Long.parseLong(receiverId), 
                Long.parseLong(listingId), 
                message);
            return ResponseEntity.status(201).body(chat);

        } catch (Exception e) {
            System.err.println("Exception "+ e);
        }
        return ResponseEntity.status(400).body("createChat error");
    }
}