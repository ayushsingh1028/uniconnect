package com.uniconnect.backend.controller;

import com.uniconnect.backend.entity.PYQ;
import com.uniconnect.backend.service.PYQService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/pyqs")
@RequiredArgsConstructor
public class PYQController {

    private final PYQService pyqService;

    @PostMapping("/upload")
    public ResponseEntity<PYQ> uploadPYQ(
            @RequestParam String subject,
            @RequestParam Integer year,
            @RequestParam String examType,
            @RequestParam("file") MultipartFile file,
            Authentication auth) throws IOException {
        return ResponseEntity.ok(pyqService.uploadPYQ(subject, year, examType, file, auth));
    }

    @GetMapping
    public ResponseEntity<List<PYQ>> searchPYQs(
            @RequestParam Long universityId,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(pyqService.searchPYQs(universityId, subject, year));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePYQ(@PathVariable Long id, Authentication auth) {
        pyqService.deletePYQ(id, auth);
        return ResponseEntity.ok().build();
    }
}
