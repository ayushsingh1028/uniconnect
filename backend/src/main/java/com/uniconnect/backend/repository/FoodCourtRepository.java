package com.uniconnect.backend.repository;

import com.uniconnect.backend.entity.FoodCourt;
import com.uniconnect.backend.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodCourtRepository extends JpaRepository<FoodCourt, Long> {
    List<FoodCourt> findByUniversity(University university);
}
