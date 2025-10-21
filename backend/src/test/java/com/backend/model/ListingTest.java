package com.backend.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class ListingTest {

    @Test
    void testListingConstructorAndGetters() {
        User seller = new User();  
        Listing listing = new Listing(
            "Laptop", 
            "A good laptop", 
            999.99, 
            List.of("photo1.jpg"), 
            Category.ELECTRONICS, 
            seller
        );

        assertEquals("Laptop", listing.getTitle());
        assertEquals("A good laptop", listing.getDescription());
        assertEquals(999.99, listing.getPrice());
        assertEquals(List.of("photo1.jpg"), listing.getPhotos());
        assertEquals(Category.ELECTRONICS, listing.getCategory());
        assertEquals(seller, listing.getSeller());
        assertEquals(Status.DRAFT, listing.getStatus());
    }

    @Test
    void testSetters() {
        Listing listing = new Listing();
        listing.setTitle("Phone");
        listing.setPrice(500.0);

        assertEquals("Phone", listing.getTitle());
        assertEquals(500.0, listing.getPrice());
    }
}