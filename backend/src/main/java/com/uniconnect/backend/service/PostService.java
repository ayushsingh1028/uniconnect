package com.uniconnect.backend.service;

import com.uniconnect.backend.dto.PostCreateRequest;
import com.uniconnect.backend.entity.Post;
import com.uniconnect.backend.entity.Comment;
import com.uniconnect.backend.entity.University;
import com.uniconnect.backend.entity.User;
import com.uniconnect.backend.entity.enums.PostType;
import com.uniconnect.backend.exception.ResourceNotFoundException;
import com.uniconnect.backend.repository.PostRepository;
import com.uniconnect.backend.repository.CommentRepository;
import com.uniconnect.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public Post createPost(PostCreateRequest request, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getUniversity() == null) {
            throw new IllegalStateException("User must be associated with a university");
        }

        Post post = new Post();
        post.setUser(user);
        post.setUniversity(user.getUniversity());
        post.setContent(request.getContent());
        post.setType(request.getType() != null ? PostType.valueOf(request.getType()) : PostType.NORMAL);
        post.setAnonymous(request.getIsAnonymous());

        return postRepository.save(post);
    }

    public Page<Post> getFeed(Long universityId, PostType type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (universityId == null) {
            return postRepository.findAll(pageable);
        }
        University university = new University();
        university.setId(universityId);

        if (type != null) {
            return postRepository.findByUniversityAndTypeOrderByCreatedAtDesc(university, type, pageable);
        }
        return postRepository.findByUniversityOrderByCreatedAtDesc(university, pageable);
    }

    @Transactional
    public Post likePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        post.setLikeCount(post.getLikeCount() + 1);
        return postRepository.save(post);
    }

    @Transactional
    public Comment addComment(Long postId, String content, Authentication auth) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content);
        comment.setAnonymous(post.isAnonymous());

        return commentRepository.save(comment);
    }

    @Transactional
    public void deletePost(Long postId, Authentication auth) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("You can only delete your own posts");
        }

        postRepository.delete(post);
    }

    @Transactional
    public void deleteComment(Long commentId, Authentication auth) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    public Page<Post> searchPosts(Long universityId, String query, int page, int size) {
        University university = new University();
        university.setId(universityId);
        return postRepository.findByUniversityAndContentContainingIgnoreCaseOrderByCreatedAtDesc(university, query,
                PageRequest.of(page, size));
    }

    public java.util.List<java.util.Map<String, Object>> getTopContributors(Long universityId) {
        University university = new University();
        university.setId(universityId);
        java.util.List<Object[]> results = postRepository.findTopContributors(university,
                org.springframework.data.domain.PageRequest.of(0, 5));

        return results.stream().map(result -> {
            User user = (User) result[0];
            Long count = (Long) result[1];
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", user.getId());
            map.put("name", user.getName());
            map.put("postCount", count);
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }
}
