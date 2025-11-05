package com.backend.e2e;

import io.restassured.RestAssured;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;

import com.backend.model.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.Map;
import java.util.HashMap;
import java.time.Instant;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.Mockito.when;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {com.backend.BackendApplication.class, ListingE2ETest.JwtTestConfig.class}
)
public class ListingE2ETest {

    @LocalServerPort
    private int port;

    private String baseUrl;

    private Listing test_listing;

    @MockBean
    private com.backend.service.UserService userService;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port + "/api/listings";
        RestAssured.port = port;

        when(userService.getUserId("c9f9f9be-c031-70f3-1f8d-2a5e94d753b6")).thenReturn(1L);

        // Create a listing to use in test
        test_listing = 
            given()
                .header("Authorization", "Bearer TEST_TOKEN")
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
            .header("Authorization", "Bearer TEST_TOKEN")
            .contentType("application/json")
        .when()
            .delete(baseUrl + "/delete/" + test_listing.getId())
        .then()
            .statusCode(200);
    }
    
    @Test
    void testGetListing() {
        given()
            .header("Authorization", "Bearer TEST_TOKEN")
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
            .header("Authorization", "Bearer TEST_TOKEN")
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
            .header("Authorization", "Bearer TEST_TOKEN")
            .contentType("application/json")
        .when()
            .get(baseUrl + "/999")  // non-existing ID
        .then()
            .statusCode(400)
            .body(equalTo("Listing not found"));
    }

    @TestConfiguration
    static class JwtTestConfig {
        @Bean
        public JwtDecoder jwtDecoder() {
            return token -> new Jwt(
                token,
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),              // must not be empty
                Map.of("sub", "c9f9f9be-c031-70f3-1f8d-2a5e94d753b6")
            );
        }
}
}