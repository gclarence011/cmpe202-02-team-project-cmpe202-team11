package com.backend.controller;

import com.backend.dto.ListingsResult;
import com.backend.model.Category;
import com.backend.model.Listing;
import com.backend.service.ListingService;
import com.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ListingControllerTest {

    private ListingService listingService;
    private UserService userService;
    private ListingController listingController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        listingService = mock(ListingService.class);
        userService = mock(UserService.class);
        listingController = new ListingController(userService, listingService);
        mockMvc = MockMvcBuilders.standaloneSetup(listingController).build();
    }

    // ✅ test hello endpoint
    @Test
    void testHello() {
        when(listingService.hello()).thenReturn("Listing Service Hello");
        when(userService.hello()).thenReturn("User Service Hello");

        String result = listingController.hello();

        assertTrue(result.contains("Listing Service Hello"));
        assertTrue(result.contains("User Service Hello"));
    }

    // ✅ test createListing success
    @Test
    void testCreateListing_Success() {
        Listing created_listing= new Listing();
        when(listingService.createListing(1L)).thenReturn(created_listing);

        ResponseEntity<?> response = listingController.createListing();

        assertEquals(201, response.getStatusCode().value());
        assertEquals(created_listing, response.getBody());
    }

    // ❌ test createListing failure (IllegalArgumentException)
    @Test
    void testCreateListing_BadRequest() {
        doThrow(new IllegalArgumentException("Invalid data"))
                .when(listingService).createListing(1L);

        ResponseEntity<?> response = listingController.createListing();

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid data", response.getBody());
    }

    // ✅ test getListing success
    @Test
    void testGetListing_Success() {
        Listing mockListing = new Listing();
        when(listingService.getListing(10L)).thenReturn(mockListing);

        ResponseEntity<?> response = listingController.getListing(10L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockListing, response.getBody());
    }

    // ❌ test getListing not found
    @Test
    void testGetListing_NotFound() {
        when(listingService.getListing(10L))
                .thenThrow(new IllegalArgumentException("Listing not found"));

        ResponseEntity<?> response = listingController.getListing(10L);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Listing not found", response.getBody());
    }

    // ✅ test updateListing success
    @Test
    void testUpdateListing_Success() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", "Updated Title");

        Listing updatedListing = new Listing();
        when(listingService.updateListing(5L, updates, 1L)).thenReturn(updatedListing);

        ResponseEntity<?> response = listingController.updateListing(5L, updates);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(updatedListing, response.getBody());
    }

    // ❌ test updateListing bad request
    @Test
    void testUpdateListing_BadRequest() {
        Map<String, Object> updates = new HashMap<>();
        doThrow(new IllegalArgumentException("Invalid update"))
                .when(listingService).updateListing(5L, updates, 1L);

        ResponseEntity<?> response = listingController.updateListing(5L, updates);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid update", response.getBody());
    }

    // ✅ test deleteListing success
    @Test
    void testDeleteListing_Success() {
        doNothing().when(listingService).deleteListing(7L, 1L);

        ResponseEntity<String> response = listingController.deleteListing(7L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Deleted Succesfully", response.getBody());
    }

    // ✅ test flagListing success
    @Test
    void testFlagListing_Success() {
        Listing flagged = new Listing();
        when(listingService.flagListing(2L, 1L)).thenReturn(flagged);

        ResponseEntity<?> response = listingController.flagListing(2L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(flagged, response.getBody());
    }

    // ✅ test searchListings success
    @Test
    void testSearchListings_Success() {
        ListingsResult result = new ListingsResult();
        when(listingService.searchListings("bike", Category.ELECTRONICS, 1L))
                .thenReturn(result);

        ResponseEntity<?> response = listingController.getListings("bike", Category.ELECTRONICS);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(result, response.getBody());
    }

    // ❌ test searchListings no result
    @Test
    void testSearchListings_NotFound() {
        when(listingService.searchListings("bike", Category.ELECTRONICS, 1L))
                .thenThrow(new IllegalArgumentException("No listings found"));

        ResponseEntity<?> response = listingController.getListings("bike", Category.ELECTRONICS);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("No listings found", response.getBody());
    }

    // ✅ test upload endpoint success
    @Test
    void testUpload_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "image-content".getBytes());
        String imageUrl = "http://example.com/image.jpg";

        when(listingService.uploadImage(any(MultipartFile.class), any(Long.class), any(Long.class))).thenReturn(imageUrl);

        Long listingId = 1L;
        mockMvc.perform(multipart("/api/listings/" + listingId + "/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string(imageUrl));
    }

    // ❌ test upload endpoint IllegalArgumentException handling
    @Test
    void testUpload_BadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "image-content".getBytes());

        when(listingService.uploadImage(any(MultipartFile.class), any(Long.class), any(Long.class))).thenThrow(new IllegalArgumentException("Invalid file"));

        Long listingId = 1L;
        mockMvc.perform(multipart("/api/listings/" + listingId + "/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid file"));
    }

    // ❌ test upload endpoint generic Exception handling
    @Test
    void testUpload_InternalServerError() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "image-content".getBytes());

        when(listingService.uploadImage(any(MultipartFile.class), any(Long.class), any(Long.class))).thenThrow(new RuntimeException("Server error"));

        Long listingId = 1L;
        mockMvc.perform(multipart("/api/listings/" + listingId + "/upload").file(file))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An unexpected error occuredServer error"));
    }
}