package com.backend.service;

import java.io.IOException;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


@Service
public class ImageService {
  @Autowired
  private S3Client s3Client;

  @Value("${aws.bucket.name}")
  private String bucketName;

  public String uploadImage(MultipartFile file) throws IOException{
  // Generate unique name while keeping file extension
    String originalName = file.getOriginalFilename();
    String extension = "";

    if (originalName != null && originalName.contains(".")) {
      extension = originalName.substring(originalName.lastIndexOf("."));
    }

    String uniqueFileName = UUID.randomUUID().toString() + extension;

    s3Client.putObject(PutObjectRequest.builder()
            .bucket(bucketName)
            .key(uniqueFileName)
            .contentType(file.getContentType())
            .contentDisposition("inline")
            .build(),
            RequestBody.fromBytes(file.getBytes()));
    return "https://" + bucketName + ".s3." + "us-east-2" + ".amazonaws.com/" + uniqueFileName;
  }

  public void deleteImage(String imageURL){
    if (imageURL == null || imageURL.isEmpty()){
      throw new IllegalArgumentException("Image URL cannot be empty");
    }

    String key = imageURL.substring(imageURL.lastIndexOf("/")+1);
    try{
      s3Client.deleteObject(builder->builder.bucket(bucketName).key(key));
    } catch (Exception e){
      throw new RuntimeException("Failed to delete image: " + e.getMessage(), e);
    }
  }
}
