package com.uniconnect.backend.controller;

import com.uniconnect.backend.entity.Event;
import com.uniconnect.backend.entity.University;
import com.uniconnect.backend.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventRepository eventRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        return ResponseEntity.ok(eventRepository.save(event));
    }

    @GetMapping
    public ResponseEntity<List<Event>> getEvents(@RequestParam Long universityId) {
        University university = new University();
        university.setId(universityId);
        return ResponseEntity.ok(eventRepository.findByUniversityOrderByEventDateDesc(university));
    }
}
