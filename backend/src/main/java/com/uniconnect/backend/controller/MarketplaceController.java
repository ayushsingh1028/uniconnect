package com.uniconnect.backend.controller;

import com.uniconnect.backend.entity.MarketplaceItem;
import com.uniconnect.backend.service.MarketplaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marketplace")
@RequiredArgsConstructor
public class MarketplaceController {

    private final MarketplaceService marketplaceService;

    @PostMapping("/items")
    public ResponseEntity<MarketplaceItem> createItem(@RequestBody MarketplaceItem item, Authentication auth) {
        return ResponseEntity.ok(marketplaceService.createItem(item, auth));
    }

    @GetMapping("/items")
    public ResponseEntity<List<MarketplaceItem>> getItems(
            @RequestParam Long universityId,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(marketplaceService.getItems(universityId, category));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id, Authentication auth) {
        marketplaceService.deleteItem(id, auth);
        return ResponseEntity.ok().build();
    }
}
