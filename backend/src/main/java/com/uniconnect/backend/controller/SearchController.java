package com.uniconnect.backend.controller;

import com.uniconnect.backend.entity.MarketplaceItem;
import com.uniconnect.backend.entity.Post;
import com.uniconnect.backend.service.MarketplaceService;
import com.uniconnect.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final PostService postService;
    private final MarketplaceService marketplaceService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam Long universityId,
            @RequestParam String query) {

        Page<Post> posts = postService.searchPosts(universityId, query, 0, 10);
        List<MarketplaceItem> items = marketplaceService.searchItems(universityId, query);

        Map<String, Object> results = new HashMap<>();
        results.put("posts", posts.getContent());
        results.put("marketplace", items);

        return ResponseEntity.ok(results);
    }
}
