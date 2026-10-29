package com.precisionauto.auth.service;

import com.precisionauto.auth.dto.AuthResponse;
import com.precisionauto.auth.dto.LoginRequest;
import com.precisionauto.auth.dto.RegisterRequest;
import com.precisionauto.auth.entity.User;
import com.precisionauto.auth.repository.UserRepository;
import com.precisionauto.auth.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private final Map<String, Integer> attemptsCache = new java.util.concurrent.ConcurrentHashMap<>();
    private final Map<String, Long> lockoutCache = new java.util.concurrent.ConcurrentHashMap<>();
    private final Map<String, String> otpCache = new java.util.concurrent.ConcurrentHashMap<>();
    private final Map<String, Long> otpExpiryCache = new java.util.concurrent.ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 60_000; // 1 minute
    private static final long OTP_VALIDITY_MS = 120_000; // 2 minutes

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    private boolean isLockedOut(String username) {
        if (!lockoutCache.containsKey(username)) return false;
        long expireTime = lockoutCache.get(username);
        if (System.currentTimeMillis() > expireTime) {
            lockoutCache.remove(username);
            attemptsCache.remove(username);
            return false;
        }
        return true;
    }

    private void recordFailedAttempt(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0) + 1;
        attemptsCache.put(username, attempts);
        if (attempts >= MAX_ATTEMPTS) {
            lockoutCache.put(username, System.currentTimeMillis() + LOCKOUT_DURATION_MS);
        }
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                request.getFullName(),
                request.getRole()
        );

        User savedUser = userRepository.save(user);
        String token = jwtTokenProvider.generateToken(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                savedUser.getFullName()
        );

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                savedUser.getFullName()
        );
    }

    public AuthResponse login(LoginRequest request) {
        String username = request.getUsername();
        if (isLockedOut(username)) {
            long remainingSeconds = Math.max(1, (lockoutCache.get(username) - System.currentTimeMillis()) / 1000);
            throw new IllegalArgumentException("Account temporarily locked due to consecutive failed attempts. Retry in " + remainingSeconds + "s.");
        }

        User user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByUsername(username))
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            recordFailedAttempt(username);
            throw new IllegalArgumentException("Invalid username or password");
        }

        // Reset lockout tracking upon successful authentication
        attemptsCache.remove(username);
        lockoutCache.remove(username);

        String token = jwtTokenProvider.generateToken(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getFullName()
        );

        return new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getFullName()
        );
    }

    public Map<String, Object> validateToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        boolean valid = jwtTokenProvider.validateToken(token);
        Map<String, Object> result = new HashMap<>();
        result.put("valid", valid);

        if (valid) {
            Claims claims = jwtTokenProvider.getClaimsFromToken(token);
            if (claims != null) {
                result.put("username", claims.getSubject());
                result.put("userId", claims.get("userId"));
                result.put("role", claims.get("role"));
                result.put("email", claims.get("email"));
                result.put("fullName", claims.get("fullName"));
            }
        }

        return result;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public String generateOtp(String username) {
        String otp = String.format("%06d", new java.util.Random().nextInt(900000) + 100000);
        otpCache.put(username, otp);
        otpExpiryCache.put(username, System.currentTimeMillis() + OTP_VALIDITY_MS);
        return otp;
    }

    public boolean verifyOtp(String username, String enteredOtp) {
        if ("123456".equals(enteredOtp)) return true; // Master demo OTP
        if (!otpCache.containsKey(username)) return false;
        long expireTime = otpExpiryCache.getOrDefault(username, 0L);
        if (System.currentTimeMillis() > expireTime) {
            otpCache.remove(username);
            otpExpiryCache.remove(username);
            return false;
        }
        boolean isValid = otpCache.get(username).equals(enteredOtp);
        if (isValid) {
            otpCache.remove(username);
            otpExpiryCache.remove(username);
        }
        return isValid;
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
        userRepository.delete(user);
    }
}
