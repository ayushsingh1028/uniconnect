package com.uniconnect.backend.service;

import com.uniconnect.backend.dto.AuthResponse;
import com.uniconnect.backend.dto.LoginRequest;
import com.uniconnect.backend.dto.RegisterRequest;
import com.uniconnect.backend.entity.University;
import com.uniconnect.backend.entity.User;
import com.uniconnect.backend.entity.enums.Role;
import com.uniconnect.backend.exception.BadRequestException;
import com.uniconnect.backend.exception.ResourceNotFoundException;
import com.uniconnect.backend.repository.UniversityRepository;
import com.uniconnect.backend.repository.UserRepository;
import com.uniconnect.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);

        if (request.getUniversityId() != null) {
            University university = universityRepository.findById(request.getUniversityId())
                    .orElseThrow(() -> new ResourceNotFoundException("University not found"));
            user.setUniversity(university);
        }

        user.setGraduationYear(request.getGraduationYear());

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(
                savedUser.getEmail(),
                savedUser.getId(),
                savedUser.getRole().name());

        return new AuthResponse(token, savedUser.getEmail(), savedUser.getName(), savedUser.getRole().name(),
                savedUser.getUniversity() != null ? savedUser.getUniversity().getId() : null,
                savedUser.getId());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getId(),
                user.getRole().name());

        return new AuthResponse(token, user.getEmail(), user.getName(), user.getRole().name(),
                user.getUniversity() != null ? user.getUniversity().getId() : null,
                user.getId());
    }

    @Transactional
    public AuthResponse googleOAuth(String email, String name, String picture, String googleId) {
        User user = userRepository.findByOauthId(googleId).orElse(null);

        if (user == null) {
            user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setProfilePicture(picture);
                user.setOauthProvider("GOOGLE");
                user.setOauthId(googleId);
                user.setRole(Role.STUDENT);
                user = userRepository.save(user);
            } else {
                user.setOauthProvider("GOOGLE");
                user.setOauthId(googleId);
                if (picture != null) {
                    user.setProfilePicture(picture);
                }
                user = userRepository.save(user);
            }
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getId(),
                user.getRole().name());

        return new AuthResponse(token, user.getEmail(), user.getName(), user.getRole().name(),
                user.getUniversity() != null ? user.getUniversity().getId() : null,
                user.getId());
    }
}
