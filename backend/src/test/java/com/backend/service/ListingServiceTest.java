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

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListingServiceTest {

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ListingService listingService;

    private User mockUser;
    private Listing mockListing;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        //mockUser.setId(1L);

        List<String> photos = new ArrayList<>(List.of("test1.jpg", "test2.jpg", "test3.jpg"));

        mockListing = new Listing();
        mockListing.setSeller(mockUser);
        mockListing.setTitle("Test Listing");
        mockListing.setStatus(Status.DRAFT);
        mockListing.setPhotos(photos);
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
    @Test
    void testUpdateListing_ValidPhotos() {
        List<String> newPhotos = List.of("test1.jpg", "test2.jpg", "test4.jpg");
        List<String> expectedPhotos = List.of("test1.jpg", "test2.jpg");
        Map<String, Object> updates = Map.of("photos", newPhotos);

        when(listingRepository.findById(100L)).thenReturn(Optional.of(mockListing));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(listingRepository.save(any(Listing.class))).thenReturn(mockListing);

        doNothing().when(imageService).deleteImage(anyString());

        Listing result = listingService.updateListing(100L, updates, 1L);

        assertEquals(expectedPhotos, result.getPhotos());
    }

    // ---------------- uploadImage() ----------------
    @Test
    void testUploadImage_Success() throws IOException {
        Long listingId = 100L;
        Long userId = 1L;

        // Mock image file
        MultipartFile mockImage = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "fake image data".getBytes());

        // Setup mock listing and user
        mockListing.setSeller(mockUser);
        mockListing.setPhotos(new ArrayList<>(List.of("existing1.jpg")));

        when(listingRepository.findById(listingId)).thenReturn(Optional.of(mockListing));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(imageService.uploadImage(mockImage)).thenReturn("https://s3.amazonaws.com/bucket/test.jpg");
        when(listingRepository.save(any(Listing.class))).thenReturn(mockListing);

        // Act
        String resultUrl = listingService.uploadImage(mockImage, listingId, userId);

        // Assert
        assertEquals("https://s3.amazonaws.com/bucket/test.jpg", resultUrl);
        assertTrue(mockListing.getPhotos().contains(resultUrl));
        verify(imageService, times(1)).uploadImage(mockImage);
        verify(listingRepository, times(1)).save(mockListing);
    }
    @Test
    void testUploadImage_ListingNotFound() {
        when(listingRepository.findById(100L)).thenReturn(Optional.empty());

        MultipartFile mockImage = new MockMultipartFile("file", "test.jpg", "image/jpeg", "data".getBytes());

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> listingService.uploadImage(mockImage, 100L, 1L));

        assertEquals("Listing not found", e.getMessage());
    }
    @Test
    void testUploadImage_UnauthorizedUser() {
        User anotherUser = new User();
        when(listingRepository.findById(100L)).thenReturn(Optional.of(mockListing));
        when(userRepository.findById(1L)).thenReturn(Optional.of(anotherUser));
        MultipartFile mockImage = new MockMultipartFile("file", "test.jpg", "image/jpeg", "data".getBytes());

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> listingService.uploadImage(mockImage, 100L, 1L));

        assertEquals("User is not the seller", e.getMessage());
    }


    // ---------------- deleteListing() ----------------
    @Test
    void testDeleteListing_Success() {
        when(listingRepository.findById(100L)).thenReturn(Optional.of(mockListing));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        doNothing().when(imageService).deleteImage(anyString());

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