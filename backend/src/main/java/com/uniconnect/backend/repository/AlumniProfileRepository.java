package com.uniconnect.backend.repository;

import com.uniconnect.backend.entity.AlumniProfile;
import com.uniconnect.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlumniProfileRepository extends JpaRepository<AlumniProfile, Long> {

    @Query("SELECT a FROM AlumniProfile a WHERE a.user.university.id = :universityId")
    List<AlumniProfile> findByUniversityId(@Param("universityId") Long universityId);
}
