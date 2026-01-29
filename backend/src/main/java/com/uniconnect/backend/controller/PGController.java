package com.uniconnect.backend.controller;

import com.uniconnect.backend.entity.PG;
import com.uniconnect.backend.entity.University;
import com.uniconnect.backend.repository.PGRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pg")
@RequiredArgsConstructor
public class PGController {

    private final PGRepository pgRepository;

    @PostMapping
    public ResponseEntity<PG> addPG(@RequestBody PG pg) {
        return ResponseEntity.ok(pgRepository.save(pg));
    }

    @GetMapping
    public ResponseEntity<List<PG>> getPGs(@RequestParam Long universityId) {
        University university = new University();
        university.setId(universityId);
        return ResponseEntity.ok(pgRepository.findByUniversity(university));
    }
}
