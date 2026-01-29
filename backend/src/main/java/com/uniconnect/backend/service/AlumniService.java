package com.uniconnect.backend.service;

import com.uniconnect.backend.entity.AlumniProfile;
import com.uniconnect.backend.entity.enums.Role;
import com.uniconnect.backend.entity.User;
import com.uniconnect.backend.exception.ResourceNotFoundException;
import com.uniconnect.backend.repository.AlumniProfileRepository;
import com.uniconnect.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlumniService {

    private final AlumniProfileRepository alumniProfileRepository;
    private final UserRepository userRepository;

    @Transactional
    public AlumniProfile createProfile(AlumniProfile profile, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRole(Role.ALUMNI);
        userRepository.save(user);

        profile.setUser(user);
        return alumniProfileRepository.save(profile);
    }

    public List<AlumniProfile> getAlumni(Long universityId) {
        return alumniProfileRepository.findByUniversityId(universityId);
    }
}
