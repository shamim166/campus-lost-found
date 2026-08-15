package com.lostfound.repository;

import com.lostfound.model.Category;
import com.lostfound.model.Item;
import com.lostfound.model.ItemStatus;
import com.lostfound.model.ItemType;
import com.lostfound.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // Find all items by user
    List<Item> findByUserOrderByCreatedAtDesc(User user);

    // Find all items by type
    List<Item> findByTypeAndStatusOrderByCreatedAtDesc(ItemType type, ItemStatus status);

    // Find by category and status
    List<Item> findByCategoryAndStatus(Category category, ItemStatus status);

    // Count statistics
    long countByType(ItemType type);

    long countByStatus(ItemStatus status);

    long countByTypeAndStatus(ItemType type, ItemStatus status);

    // Browse page: search + filters with pagination
    @Query("SELECT i FROM Item i WHERE " +
           "(:keyword IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', CAST(:keyword AS String), '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', CAST(:keyword AS String), '%'))) AND " +
           "(:type IS NULL OR i.type = :type) AND " +
           "(:category IS NULL OR i.category = :category) AND " +
           "(:location IS NULL OR LOWER(i.location) LIKE LOWER(CONCAT('%', CAST(:location AS String), '%'))) AND " +
           "(:dateFrom IS NULL OR i.itemDate >= :dateFrom) AND " +
           "(:dateTo IS NULL OR i.itemDate <= :dateTo) AND " +
           "i.status = 'ACTIVE' " +
           "ORDER BY i.createdAt DESC")
    Page<Item> browseItems(
            @Param("keyword") String keyword,
            @Param("type") ItemType type,
            @Param("category") Category category,
            @Param("location") String location,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            Pageable pageable
    );

    // Matching: find active FOUND items in same category near the same location
    @Query("SELECT i FROM Item i WHERE i.type = 'FOUND' AND i.status = 'ACTIVE' " +
           "AND i.category = :category " +
           "AND LOWER(i.location) LIKE LOWER(CONCAT('%', :locationKeyword, '%'))")
    List<Item> findPotentialFoundMatches(
            @Param("category") Category category,
            @Param("locationKeyword") String locationKeyword
    );

    // Matching: find active LOST items in same category near the same location
    @Query("SELECT i FROM Item i WHERE i.type = 'LOST' AND i.status = 'ACTIVE' " +
           "AND i.category = :category " +
           "AND LOWER(i.location) LIKE LOWER(CONCAT('%', :locationKeyword, '%'))")
    List<Item> findPotentialLostMatches(
            @Param("category") Category category,
            @Param("locationKeyword") String locationKeyword
    );

    // Recent active items for home page
    List<Item> findTop8ByStatusOrderByCreatedAtDesc(ItemStatus status);

    // User-specific counts
    long countByUserAndType(User user, ItemType type);

    long countByUserAndStatus(User user, ItemStatus status);

}
