package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.backend.model.Listing;
import com.backend.model.Category;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {
  List<Listing> findAllBySellerId(Long id);
  
  // Natural language search methods
  @Query("SELECT l FROM Listing l WHERE " +
         "(LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
         "LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
         "AND l.status = 'OPEN'")
  List<Listing> searchByKeyword(@Param("keyword") String keyword);
  
  @Query("SELECT l FROM Listing l WHERE " +
         "l.price >= :minPrice AND l.price <= :maxPrice " +
         "AND l.status = 'OPEN'")
  List<Listing> searchByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
  
  @Query("SELECT l FROM Listing l WHERE " +
         "l.category = :category " +
         "AND l.status = 'OPEN'")
  List<Listing> searchByCategory(@Param("category") Category category);
  
  @Query("SELECT l FROM Listing l WHERE " +
         "(LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
         "LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
         "AND l.price >= :minPrice AND l.price <= :maxPrice " +
         "AND l.status = 'OPEN'")
  List<Listing> searchByKeywordAndPrice(
      @Param("keyword") String keyword,
      @Param("minPrice") Double minPrice,
      @Param("maxPrice") Double maxPrice);
  
  @Query("SELECT l FROM Listing l WHERE " +
         "(LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
         "LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
         "AND l.category = :category " +
         "AND l.status = 'OPEN'")
  List<Listing> searchByKeywordAndCategory(
      @Param("keyword") String keyword,
      @Param("category") Category category);
  
  @Query("SELECT l FROM Listing l WHERE " +
         "(LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
         "LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
         "AND l.price >= :minPrice AND l.price <= :maxPrice " +
         "AND l.category = :category " +
         "AND l.status = 'OPEN'")
  List<Listing> searchByCriteria(
      @Param("keyword") String keyword,
      @Param("minPrice") Double minPrice,
      @Param("maxPrice") Double maxPrice,
      @Param("category") Category category);
  
  @Query("SELECT l FROM Listing l WHERE l.status = 'OPEN'")
  List<Listing> findAllActive();
}