package com.uniconnect.backend.service;

import com.uniconnect.backend.entity.PYQ;
import com.uniconnect.backend.entity.University;
import com.uniconnect.backend.entity.User;
import com.uniconnect.backend.exception.ResourceNotFoundException;
import com.uniconnect.backend.repository.PYQRepository;
import com.uniconnect.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PYQService {

    private final PYQRepository pyqRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public PYQ uploadPYQ(String subject, Integer year, String examType, MultipartFile file, Authentication auth)
            throws IOException {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getUniversity() == null) {
            throw new IllegalStateException("User must be associated with a university");
        }

        Map<String, Object> uploadResult = cloudinaryService.upload(file, "pyqs");
        if (uploadResult == null || uploadResult.get("secure_url") == null) {
            throw new RuntimeException("Failed to upload file to Cloudinary");
        }

        PYQ pyq = new PYQ();
        pyq.setSubject(subject);
        pyq.setYear(year);
        pyq.setExamType(examType);
        pyq.setFileUrl(uploadResult.get("secure_url").toString());
        pyq.setFilePublicId(uploadResult.get("public_id") != null ? uploadResult.get("public_id").toString() : null);
        pyq.setUploadedBy(user);
        pyq.setUniversity(user.getUniversity());

        return pyqRepository.save(pyq);
    }

    public List<PYQ> searchPYQs(Long universityId, String subject, Integer year) {
        University university = new University();
        university.setId(universityId);

        if (subject != null && !subject.isEmpty()) {
            return pyqRepository.findByUniversityAndSubjectContainingIgnoreCase(university, subject);
        } else if (year != null) {
            return pyqRepository.findByUniversityAndYear(university, year);
        } else {
            return pyqRepository.findByUniversity(university);
        }
    }

    @Transactional
    public void deletePYQ(Long id, Authentication auth) {
        PYQ pyq = pyqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PYQ not found"));

        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!pyq.getUploadedBy().getId().equals(user.getId())) {
            throw new IllegalStateException("You can only delete your own uploads");
        }

        if (pyq.getFilePublicId() != null) {
            try {
                cloudinaryService.delete(pyq.getFilePublicId());
            } catch (IOException e) {
                System.err.println("Failed to delete file from Cloudinary: " + e.getMessage());
            }
        }

        pyqRepository.delete(pyq);
    }
}
