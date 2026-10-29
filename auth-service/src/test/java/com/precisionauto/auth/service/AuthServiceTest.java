package com.precisionauto.auth.service;

import com.precisionauto.auth.dto.AuthResponse;
import com.precisionauto.auth.dto.LoginRequest;
import com.precisionauto.auth.dto.RegisterRequest;
import com.precisionauto.auth.entity.Role;
import com.precisionauto.auth.entity.User;
import com.precisionauto.auth.repository.UserRepository;
import com.precisionauto.auth.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User("johndoe", "hashed_pwd", "john@precisionauto.com", "John Doe", Role.ROLE_CUSTOMER);
        sampleUser.setId(1L);
    }

    @Test
    @DisplayName("Should successfully register new user and return JWT token")
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest("johndoe", "plain_pwd", "john@precisionauto.com", "John Doe", Role.ROLE_CUSTOMER);

        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@precisionauto.com")).thenReturn(false);
        when(passwordEncoder.encode("plain_pwd")).thenReturn("hashed_pwd");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(jwtTokenProvider.generateToken(any(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn("mocked.jwt.token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getToken());
        assertEquals("johndoe", response.getUsername());
        assertEquals("ROLE_CUSTOMER", response.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if username already exists during registration")
    void testRegister_DuplicateUsername() {
        RegisterRequest request = new RegisterRequest("johndoe", "plain_pwd", "john@precisionauto.com", "John Doe", Role.ROLE_CUSTOMER);
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should authenticate user with valid credentials and return JWT token")
    void testLogin_Success() {
        LoginRequest request = new LoginRequest("johndoe", "plain_pwd");

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("plain_pwd", "hashed_pwd")).thenReturn(true);
        when(jwtTokenProvider.generateToken(any(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn("mocked.jwt.token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getToken());
        assertEquals(1L, response.getUserId());
    }

    @Test
    @DisplayName("Should reject login with incorrect password")
    void testLogin_InvalidPassword() {
        LoginRequest request = new LoginRequest("johndoe", "wrong_pwd");

        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrong_pwd", "hashed_pwd")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("Should correctly validate valid JWT token")
    void testValidateToken() {
        when(jwtTokenProvider.validateToken("valid.jwt")).thenReturn(true);
        io.jsonwebtoken.Claims mockClaims = mock(io.jsonwebtoken.Claims.class);
        when(mockClaims.getSubject()).thenReturn("johndoe");
        when(mockClaims.get("userId")).thenReturn(1L);
        when(mockClaims.get("role")).thenReturn("ROLE_CUSTOMER");
        when(jwtTokenProvider.getClaimsFromToken("valid.jwt")).thenReturn(mockClaims);

        Map<String, Object> result = authService.validateToken("Bearer valid.jwt");

        assertNotNull(result);
        assertEquals(true, result.get("valid"));
        assertEquals("johndoe", result.get("username"));
        assertEquals(1L, result.get("userId"));
        assertEquals("ROLE_CUSTOMER", result.get("role"));
    }
}
