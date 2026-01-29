package com.uniconnect.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.uniconnect.backend.entity.enums.PostType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostType type = PostType.NORMAL;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @com.fasterxml.jackson.annotation.JsonProperty("isAnonymous")
    @Column(name = "is_anonymous", nullable = false)
    private boolean isAnonymous = false;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
