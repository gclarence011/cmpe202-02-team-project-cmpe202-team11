package com.backend.e2e;

import io.restassured.RestAssured;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.backend.model.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.Map;
import java.util.HashMap;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = com.backend.BackendApplication.class
)
public class ListingE2ETest {

    @LocalServerPort
    private int port;

    private String baseUrl;

    private Listing test_listing;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port + "/api/listings";
        RestAssured.port = port;

        // Create a listing to use in test
        test_listing = 
            given()
                .contentType("application/json")
            .when()
                .post(baseUrl + "/create")
            .then()
                .statusCode(201)
                .extract()
                .body()
                .as(Listing.class);
    }
    @AfterEach
    void cleanup(){
        given()
            .contentType("application/json")
        .when()
            .delete(baseUrl + "/delete/" + test_listing.getId())
        .then()
            .statusCode(200);
    }
    
    @Test
    void testGetListing() {
        given()
            .contentType("application/json")
        .when()
            .get(baseUrl + "/" + test_listing.getId())
        .then()
            .statusCode(200)
            .body("id",equalTo(test_listing.getId().intValue()))
            .body("title",equalTo(test_listing.getTitle()));
    }

    @Test
    void testUpdateListing() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", "Updated Title");

        given()
            .contentType("application/json")
            .body(updates)
        .when()
            .patch(baseUrl + "/" + test_listing.getId())
        .then()
            .statusCode(200)
            .body("title", equalTo("Updated Title"));
    }

    @Test
    void testGetListing_NotFound() {
        given()
            .contentType("application/json")
        .when()
            .get(baseUrl + "/999")  // non-existing ID
        .then()
            .statusCode(400)
            .body(equalTo("Listing not found"));
    }
}