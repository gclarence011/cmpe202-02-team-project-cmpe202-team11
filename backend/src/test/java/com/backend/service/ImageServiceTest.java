package com.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;


import com.backend.service.ImageService;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    public ImageService imageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadImage_ShouldUploadAndReturnUrl() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(PutObjectResponse.builder().build());

        String url = imageService.uploadImage(file);
        System.out.println("Print URL: "+url);

        assertNotNull(url);
        assertTrue(url.contains("https://null.s3.us-east-2.amazonaws.com/"));
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void uploadImage_ShouldKeepExtension() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "photo.png", "image/png", "data".getBytes());

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(PutObjectResponse.builder().build());

        String url = imageService.uploadImage(file);

        assertTrue(url.endsWith(".png"));
    }

    @Test
    void uploadImage_ShouldHandleNoExtension() throws IOException {
        MultipartFile file = new MockMultipartFile("file", "file", "application/octet-stream", "data".getBytes());

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(PutObjectResponse.builder().build());

        String url = imageService.uploadImage(file);

        // Should not end with a dot
        assertFalse(url.endsWith("."));
    }

    @Test
    void deleteImage_ShouldCallDeleteObject() {
        String imageUrl = "https://test-bucket.s3.us-east-2.amazonaws.com/some-key.jpg";

        when(s3Client.deleteObject(any(Consumer.class))).thenReturn(DeleteObjectResponse.builder().build());

        assertDoesNotThrow(() -> imageService.deleteImage(imageUrl));

        verify(s3Client, times(1)).deleteObject(any(Consumer.class));
    }

    @Test
    void deleteImage_ShouldThrowIllegalArgumentException_WhenUrlIsNull() {
        assertThrows(IllegalArgumentException.class, () -> imageService.deleteImage(null));
    }

    @Test
    void deleteImage_ShouldThrowIllegalArgumentException_WhenUrlIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> imageService.deleteImage(""));
    }

    @Test
    void deleteImage_ShouldThrowRuntimeException_WhenDeleteFails() {
        String imageUrl = "https://test-bucket.s3.us-east-2.amazonaws.com/some-key.jpg";

        doThrow(new RuntimeException("fail")).when(s3Client).deleteObject(any(Consumer.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> imageService.deleteImage(imageUrl));
        assertTrue(ex.getMessage().contains("Failed to delete image"));
    }
}
