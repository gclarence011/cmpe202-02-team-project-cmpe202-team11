package com.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.model.Chat;
import com.backend.model.User;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findBySenderOrReceiver(User sender, User receiver);
}