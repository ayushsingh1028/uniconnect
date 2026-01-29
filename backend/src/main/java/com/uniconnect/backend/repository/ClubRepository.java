package com.uniconnect.backend.repository;

import com.uniconnect.backend.entity.Club;
import com.uniconnect.backend.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    List<Club> findByUniversity(University university);
}
