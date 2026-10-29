package com.precisionauto.auth.controller;

import com.precisionauto.auth.dto.AuthResponse;
import com.precisionauto.auth.dto.LoginRequest;
import com.precisionauto.auth.dto.RegisterRequest;
import com.precisionauto.auth.entity.User;
import com.precisionauto.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                             @RequestParam(value = "token", required = false) String tokenParam) {
        String token = authHeader != null ? authHeader : tokenParam;
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "error", "No token provided"));
        }
        Map<String, Object> result = authService.validateToken(token);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        try {
            authService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User #" + id + " successfully deleted.", "userId", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "auth-service"));
    }

    @PostMapping("/otp/send")
    public ResponseEntity<?> sendOtp(@RequestParam String username) {
        String otp = authService.generateOtp(username);
        return ResponseEntity.ok(Map.of(
                "status", "OTP_DISPATCHED",
                "message", "Two-factor authentication code dispatched for " + username,
                "channel", username + "@precisionauto.com",
                "otpPreview", otp,
                "expiresInSeconds", 120
        ));
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<?> verifyOtp(@RequestParam String username, @RequestParam String otp) {
        boolean valid = authService.verifyOtp(username, otp);
        if (valid) {
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Two-factor authentication verified successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired OTP"));
        }
    }
}
