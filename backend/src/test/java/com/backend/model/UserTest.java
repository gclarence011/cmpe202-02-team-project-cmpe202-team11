package com.backend.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testUserConstructorAndGetters() {
        String name = "test user";
        String email = "test@test.com";

        User user = new User(name,email);  

        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    void testSetters() {
        String name = "test user";
        String email = "test@test.com";
        Role role = Role.ADMIN;

        User user = new User();  
        
        user.setName(name);
        user.setEmail(email);
        user.setRole(role );

        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(role, user.getRole());
    }
}