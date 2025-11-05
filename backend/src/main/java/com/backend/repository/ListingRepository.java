package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.model.Listing;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {
  List<Listing> findAllBySellerId(Long id);
}