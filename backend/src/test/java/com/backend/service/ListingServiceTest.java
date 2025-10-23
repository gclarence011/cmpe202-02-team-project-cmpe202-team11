package com.backend.service;

import com.backend.model.*;
import com.backend.repository.ListingRepository;
import com.backend.repository.UserRepository;
import com.backend.dto.ListingsResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListingServiceTest {

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ListingService listingService;

    private User mockUser;
    private Listing mockListing;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        //mockUser.setId(1L);

        mockListing = new Listing();
        mockListing.setSeller(mockUser);
        mockListing.setTitle("Test Listing");
        mockListing.setStatus(Status.DRAFT);
    }

    // ---------------- createListing() ----------------
    @Test
    void testCreateListing_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(listingRepository.save(any(Listing.class))).thenReturn(mockListing);

        assertDoesNotThrow(() -> listingService.createListing(1L));
        verify(listingRepository, times(1)).save(any(Listing.class));
    }

    @Test
    void testCreateListing_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception e = assertThrows(IllegalArgumentException.class, () -> listingService.createListing(1L));
        assertEquals("User not found", e.getMessage());
    }

    // ---------------- getListing() ----------------
    @Test
    void testGetListing_Success() {
        when(listingRepository.findById(100L)).thenReturn(Optional.of(mockListing));

        Listing result = listingService.getListing(100L);

        assertEquals(mockListing, result);
    }

    @Test
    void testGetListing_NotFound() {
        when(listingRepository.findById(100L)).thenReturn(Optional.empty());

        Exception e = assertThrows(IllegalArgumentException.class, () -> listingService.getListing(100L));
        assertEquals("Listing not found", e.getMessage());
    }

    // ---------------- updateListing() ----------------
    @Test
    void testUpdateListing_TitleAndPriceUpdated() {
        Map<String, Object> updates = Map.of("title", "New Title", "price", "99.99");

        when(listingRepository.findById(100L)).thenReturn(Optional.of(mockListing));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(listingRepository.save(any(Listing.class))).thenReturn(mockListing);

        Listing result = listingService.updateListing(100L, updates, 1L);

        assertEquals("New Title", result.getTitle());
        assertEquals(99.99, result.getPrice());
    }

    @Test
    void testUpdateListing_InvalidPriceFormat() {
        Map<String, Object> updates = Map.of("price", "not-a-number");

        when(listingRepository.findById(100L)).thenReturn(Optional.of(mockListing));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        assertThrows(IllegalArgumentException.class, () -> listingService.updateListing(100L, updates, 1L));
    }

    @Test
    void testUpdateListing_InvalidCategory() {
        Map<String, Object> updates = Map.of("category", "invalidCategory");

        when(listingRepository.findById(100L)).thenReturn(Optional.of(mockListing));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        assertThrows(IllegalArgumentException.class, () -> listingService.updateListing(100L, updates, 1L));
    }

    // ---------------- deleteListing() ----------------
    @Test
    void testDeleteListing_Success() {
        when(listingRepository.findById(100L)).thenReturn(Optional.of(mockListing));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        assertDoesNotThrow(() -> listingService.deleteListing(100L, 1L));
        verify(listingRepository, times(1)).delete(mockListing);
    }

    @Test
    void testDeleteListing_UnauthorizedUser() {
        User anotherUser = new User();
        when(listingRepository.findById(100L)).thenReturn(Optional.of(mockListing));
        when(userRepository.findById(2L)).thenReturn(Optional.of(anotherUser));

        assertThrows(IllegalArgumentException.class, () -> listingService.deleteListing(100L, 2L));
    }

    // ---------------- flagListing() ----------------
    @Test
    void testFlagListing_ByAdmin_Success() {
        User admin = new User();
        admin.setRole(Role.ADMIN);

        when(listingRepository.findById(100L)).thenReturn(Optional.of(mockListing));
        when(userRepository.findById(99L)).thenReturn(Optional.of(admin));
        when(listingRepository.save(any(Listing.class))).thenReturn(mockListing);

        Listing result = listingService.flagListing(100L, 99L);

        assertEquals(Status.FLAGED, result.getStatus());
        verify(listingRepository).save(mockListing);
    }

    @Test
    void testFlagListing_ByNonAdmin_ThrowsException() {
        when(listingRepository.findById(100L)).thenReturn(Optional.of(mockListing));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser)); // mockUser is not admin

        assertThrows(IllegalArgumentException.class, () -> listingService.flagListing(100L, 1L));
    }
}