package com.uniconnect.backend.controller;

import com.uniconnect.backend.entity.FoodCourt;
import com.uniconnect.backend.entity.University;
import com.uniconnect.backend.repository.FoodCourtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food")
@RequiredArgsConstructor
public class FoodCourtController {

    private final FoodCourtRepository foodCourtRepository;

    @GetMapping
    public ResponseEntity<List<FoodCourt>> getFoodCourts(@RequestParam Long universityId) {
        University university = new University();
        university.setId(universityId);
        return ResponseEntity.ok(foodCourtRepository.findByUniversity(university));
    }

    @PostMapping
    public ResponseEntity<FoodCourt> addFoodCourt(@RequestBody FoodCourt foodCourt) {
        return ResponseEntity.ok(foodCourtRepository.save(foodCourt));
    }
}
