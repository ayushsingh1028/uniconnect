package com.uniconnect.backend.repository;

import com.uniconnect.backend.entity.PG;
import com.uniconnect.backend.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PGRepository extends JpaRepository<PG, Long> {
    List<PG> findByUniversity(University university);
}
