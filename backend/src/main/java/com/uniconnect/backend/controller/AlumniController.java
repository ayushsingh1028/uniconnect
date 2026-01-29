package com.uniconnect.backend.controller;

import com.uniconnect.backend.entity.AlumniProfile;
import com.uniconnect.backend.service.AlumniService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumni")
@RequiredArgsConstructor
public class AlumniController {

    private final AlumniService alumniService;

    @PostMapping("/profile")
    public ResponseEntity<AlumniProfile> createProfile(@RequestBody AlumniProfile profile, Authentication auth) {
        return ResponseEntity.ok(alumniService.createProfile(profile, auth));
    }

    @GetMapping
    public ResponseEntity<List<AlumniProfile>> getAlumni(@RequestParam Long universityId) {
        return ResponseEntity.ok(alumniService.getAlumni(universityId));
    }
}
