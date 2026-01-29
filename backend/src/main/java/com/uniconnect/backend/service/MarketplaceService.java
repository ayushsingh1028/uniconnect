package com.uniconnect.backend.service;

import com.uniconnect.backend.entity.MarketplaceItem;
import com.uniconnect.backend.entity.University;
import com.uniconnect.backend.entity.User;
import com.uniconnect.backend.exception.ResourceNotFoundException;
import com.uniconnect.backend.repository.MarketplaceItemRepository;
import com.uniconnect.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketplaceService {

    private final MarketplaceItemRepository marketplaceItemRepository;
    private final UserRepository userRepository;

    @Transactional
    public MarketplaceItem createItem(MarketplaceItem item, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        item.setSeller(user);
        item.setUniversity(user.getUniversity());
        return marketplaceItemRepository.save(item);
    }

    public List<MarketplaceItem> getItems(Long universityId, String category) {
        University university = new University();
        university.setId(universityId);

        if (category != null && !category.isEmpty()) {
            return marketplaceItemRepository.findByUniversityAndCategoryAndStatus(university, category, "AVAILABLE");
        }
        return marketplaceItemRepository.findByUniversityAndStatus(university, "AVAILABLE");
    }

    @Transactional
    public void deleteItem(Long id, Authentication auth) {
        MarketplaceItem item = marketplaceItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!item.getSeller().getId().equals(user.getId())) {
            throw new IllegalStateException("You can only delete your own items");
        }

        marketplaceItemRepository.delete(item);
    }

    public List<MarketplaceItem> searchItems(Long universityId, String query) {
        University university = new University();
        university.setId(universityId);
        return marketplaceItemRepository
                .findByUniversityAndStatusAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(university,
                        "AVAILABLE", query, query);
    }
}
