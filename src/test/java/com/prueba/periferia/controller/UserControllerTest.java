package com.prueba.periferia.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.prueba.periferia.dto.LoginRequest;
import com.prueba.periferia.dto.LoginResponse;
import com.prueba.periferia.dto.RegisterUserRequest;
import com.prueba.periferia.dto.UserResponse;
import com.prueba.periferia.service.IUserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	@Mock
	private IUserService userService;

	@InjectMocks
	private UserController userController;

	@Test
	void register_shouldReturnCreated() {
		RegisterUserRequest request = new RegisterUserRequest();
		request.setName("John");
		request.setEmail("john@example.com");
		request.setPassword("secret123");

		UserResponse body = new UserResponse(1L, "John", "john@example.com");
		when(userService.register(request)).thenReturn(body);

		ResponseEntity<UserResponse> response = userController.register(request);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(body, response.getBody());
	}

	@Test
	void login_shouldReturnOk() {
		LoginRequest request = new LoginRequest();
		request.setEmail("john@example.com");
		request.setPassword("secret123");

		LoginResponse body = new LoginResponse("token", 1L, "John", "john@example.com");
		when(userService.login(request)).thenReturn(body);

		ResponseEntity<LoginResponse> response = userController.login(request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("token", response.getBody().getToken());
	}

}
