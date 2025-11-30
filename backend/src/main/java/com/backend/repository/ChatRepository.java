package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.model.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {

}