package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.model.Listing;

public interface ListingRepository extends JpaRepository<Listing, Long> {
}