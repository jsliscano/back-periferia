package com.prueba.periferia.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.prueba.periferia.dto.LoginRequest;
import com.prueba.periferia.dto.LoginResponse;
import com.prueba.periferia.dto.RegisterUserRequest;
import com.prueba.periferia.dto.UserResponse;
import com.prueba.periferia.exception.EmailAlreadyExistsException;
import com.prueba.periferia.exception.InvalidCredentialsException;
import com.prueba.periferia.model.User;
import com.prueba.periferia.repository.UserRepository;
import com.prueba.periferia.security.JwtService;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtService jwtService;

	@InjectMocks
	private UserServiceImpl userService;

	private RegisterUserRequest registerRequest;
	private LoginRequest loginRequest;
	private User user;

	@BeforeEach
	void setUp() {
		registerRequest = new RegisterUserRequest();
		registerRequest.setName("John Doe");
		registerRequest.setEmail("john@example.com");
		registerRequest.setPassword("secret123");

		loginRequest = new LoginRequest();
		loginRequest.setEmail("john@example.com");
		loginRequest.setPassword("secret123");

		user = new User();
		user.setId(1L);
		user.setName("John Doe");
		user.setEmail("john@example.com");
		user.setPassword("hashed");
	}

	@Test
	void register_shouldCreateUser_whenEmailIsAvailable() {
		when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
		when(passwordEncoder.encode("secret123")).thenReturn("hashed");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
			User saved = invocation.getArgument(0);
			saved.setId(1L);
			return saved;
		});

		UserResponse response = userService.register(registerRequest);

		assertEquals(1L, response.getId());
		assertEquals("John Doe", response.getName());
		assertEquals("john@example.com", response.getEmail());
		verify(userRepository).save(any(User.class));
	}

	@Test
	void register_shouldThrow_whenEmailAlreadyExists() {
		when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

		assertThrows(EmailAlreadyExistsException.class, () -> userService.register(registerRequest));
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void login_shouldReturnToken_whenCredentialsAreValid() {
		when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("secret123", "hashed")).thenReturn(true);
		when(jwtService.generateToken(user)).thenReturn("jwt-token");

		LoginResponse response = userService.login(loginRequest);

		assertEquals("jwt-token", response.getToken());
		assertEquals("Bearer", response.getType());
		assertEquals(1L, response.getUserId());
		assertEquals("John Doe", response.getName());
		assertEquals("john@example.com", response.getEmail());
	}

	@Test
	void login_shouldThrow_whenUserNotFound() {
		when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

		assertThrows(InvalidCredentialsException.class, () -> userService.login(loginRequest));
		verify(jwtService, never()).generateToken(any());
	}

	@Test
	void login_shouldThrow_whenPasswordDoesNotMatch() {
		when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

		assertThrows(InvalidCredentialsException.class, () -> userService.login(loginRequest));
		verify(jwtService, never()).generateToken(any());
	}

	@Test
	void loginResponse_allArgsConstructor_shouldSetFields() {
		LoginResponse response = new LoginResponse("t", "Bearer", 2L, "Ana", "ana@test.com");
		assertNotNull(response);
		assertEquals("t", response.getToken());
		assertEquals(2L, response.getUserId());
	}

}
