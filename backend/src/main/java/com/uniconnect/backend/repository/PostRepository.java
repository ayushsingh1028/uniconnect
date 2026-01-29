package com.uniconnect.backend.repository;

import com.uniconnect.backend.entity.Post;
import com.uniconnect.backend.entity.University;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
        Page<Post> findByUniversityOrderByCreatedAtDesc(University university, Pageable pageable);

        Page<Post> findByUniversityAndTypeOrderByCreatedAtDesc(University university,
                        com.uniconnect.backend.entity.enums.PostType type, Pageable pageable);

        Page<Post> findByUniversityAndContentContainingIgnoreCaseOrderByCreatedAtDesc(University university,
                        String query,
                        Pageable pageable);

        @org.springframework.data.jpa.repository.Query("SELECT p.user, COUNT(p) as postCount FROM Post p WHERE p.university = :university GROUP BY p.user ORDER BY postCount DESC")
        java.util.List<Object[]> findTopContributors(
                        @org.springframework.data.repository.query.Param("university") University university,
                        Pageable pageable);
}
