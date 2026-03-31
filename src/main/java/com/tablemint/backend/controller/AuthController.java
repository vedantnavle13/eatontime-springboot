package com.tablemint.backend.controller;

import com.tablemint.backend.dto.request.LoginRequest;
import com.tablemint.backend.dto.request.RegisterRequest;
import com.tablemint.backend.dto.response.AuthResponse;
import com.tablemint.backend.entity.User;
import com.tablemint.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(201).body(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @GetMapping("/me")
    public ResponseEntity<User> me(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(authService.getMe(userId));
    }
}
