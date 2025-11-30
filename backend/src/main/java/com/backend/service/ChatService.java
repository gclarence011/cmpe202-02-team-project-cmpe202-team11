package com.backend.service;

import java.io.IOException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.backend.model.Chat;
import com.backend.model.User;
import com.backend.model.Listing;
import com.backend.repository.ChatRepository;
import com.backend.repository.UserRepository;
import com.backend.repository.ListingRepository;

import java.util.List;
import java.time.*;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;

    public ChatService(ChatRepository chatRepository, UserRepository userRepository, ListingRepository listingRepository){
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
    }

    public Chat createChat(long senderId, long receiverId, long listingId, String content){
        User sender = userRepository.findById(senderId).orElseThrow(() -> new IllegalArgumentException( "Sender not found"));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new IllegalArgumentException( "Receiver not found"));
        Listing listing = listingRepository.findById(listingId).orElseThrow(() -> new IllegalArgumentException( "Listing not found"));
        Chat chat = new Chat();
        chat.setTime(LocalDateTime.now());
        chat.setSender(sender);
        chat.setReceiver(receiver);
        chat.setListing(listing);
        chat.setContent(content);
        try{
            chatRepository.save(chat);
            return chat;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Invalid chat data");
        } catch (Exception e) {
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }

    public List<Chat> getChats(){//TODO change to only return ones user is involved with
        return chatRepository.findAll();
    }
}