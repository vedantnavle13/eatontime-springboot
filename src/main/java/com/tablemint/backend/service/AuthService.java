package com.tablemint.backend.service;

import com.tablemint.backend.dto.request.LoginRequest;
import com.tablemint.backend.dto.request.RegisterRequest;
import com.tablemint.backend.dto.response.AuthResponse;
import com.tablemint.backend.entity.User;
import com.tablemint.backend.exception.ConflictException;
import com.tablemint.backend.exception.UnauthorizedException;
import com.tablemint.backend.repository.UserRepository;
import com.tablemint.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new ConflictException("Email already registered");
        }

        User user = new User();
        user.setEmail(req.email());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setName(req.name());
        user.setPhone(req.phone());
        userRepository.save(user);

        String token = jwtService.generateToken(user.getId(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getName(), user.getRole());
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getId(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getName(), user.getRole());
    }

    @Transactional(readOnly = true)
    public User getMe(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }
}
