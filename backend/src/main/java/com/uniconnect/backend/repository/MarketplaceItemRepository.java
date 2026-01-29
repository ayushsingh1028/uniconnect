package com.uniconnect.backend.repository;

import com.uniconnect.backend.entity.MarketplaceItem;
import com.uniconnect.backend.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketplaceItemRepository extends JpaRepository<MarketplaceItem, Long> {
    List<MarketplaceItem> findByUniversityAndStatus(University university, String status);

    List<MarketplaceItem> findByUniversityAndCategoryAndStatus(University university, String category, String status);

    List<MarketplaceItem> findByUniversityAndStatusAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            University university, String status, String titleQuery, String descQuery);
}
