package com.uniconnect.backend.repository;

import com.uniconnect.backend.entity.PYQ;
import com.uniconnect.backend.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PYQRepository extends JpaRepository<PYQ, Long> {
    List<PYQ> findByUniversity(University university);

    List<PYQ> findByUniversityAndSubjectContainingIgnoreCase(University university, String subject);

    List<PYQ> findByUniversityAndYear(University university, Integer year);
}
