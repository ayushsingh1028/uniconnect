package com.uniconnect.backend.controller;

import com.uniconnect.backend.entity.Club;
import com.uniconnect.backend.entity.University;
import com.uniconnect.backend.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubRepository clubRepository;

    @GetMapping
    public ResponseEntity<List<Club>> getClubs(@RequestParam Long universityId) {
        University university = new University();
        university.setId(universityId);
        return ResponseEntity.ok(clubRepository.findByUniversity(university));
    }

    @PostMapping
    public ResponseEntity<Club> createClub(@RequestBody Club club) {
        return ResponseEntity.ok(clubRepository.save(club));
    }
}
