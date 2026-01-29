package com.uniconnect.backend.controller;

import com.uniconnect.backend.dto.PostCreateRequest;
import com.uniconnect.backend.entity.Comment;
import com.uniconnect.backend.entity.Post;
import com.uniconnect.backend.entity.enums.PostType;
import com.uniconnect.backend.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Post> createPost(@Valid @RequestBody PostCreateRequest request, Authentication auth) {
        return ResponseEntity.ok(postService.createPost(request, auth));
    }

    @GetMapping("/feed")
    public ResponseEntity<Page<Post>> getFeed(
            @RequestParam Long universityId,
            @RequestParam(required = false) PostType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getFeed(universityId, type, page, size));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Post> likePost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.likePost(id));
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Comment> addComment(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            Authentication auth) {
        String content = request.get("content");
        return ResponseEntity.ok(postService.addComment(id, content, auth));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Authentication auth) {
        postService.deletePost(id, auth);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, Authentication auth) {
        postService.deleteComment(commentId, auth);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/top-contributors")
    public ResponseEntity<java.util.List<java.util.Map<String, Object>>> getTopContributors(
            @RequestParam Long universityId) {
        return ResponseEntity.ok(postService.getTopContributors(universityId));
    }
}
