package com.prueba.periferia.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.prueba.periferia.model.User;

class JwtServiceTest {

	private JwtService jwtService;

	@BeforeEach
	void setUp() {
		jwtService = new JwtService(
				"periferia-jwt-secret-key-change-this-in-production-256bits",
				3_600_000L);
	}

	@Test
	void generateAndParseToken_shouldExposeClaims() {
		User user = new User();
		user.setId(5L);
		user.setName("John");
		user.setEmail("john@example.com");

		String token = jwtService.generateToken(user);

		assertEquals("john@example.com", jwtService.extractEmail(token));
		assertEquals(5L, jwtService.extractUserId(token));
		assertTrue(jwtService.isTokenValid(token));
		assertEquals("John", jwtService.parseClaims(token).get("name", String.class));
	}

	@Test
	void isTokenValid_shouldReturnFalse_forInvalidToken() {
		assertFalse(jwtService.isTokenValid("invalid.token.value"));
	}

	@Test
	void expiredToken_shouldNotBeValid() {
		JwtService shortLived = new JwtService(
				"periferia-jwt-secret-key-change-this-in-production-256bits",
				-1_000L);

		User user = new User();
		user.setId(1L);
		user.setName("John");
		user.setEmail("john@example.com");

		String token = shortLived.generateToken(user);

		assertFalse(shortLived.isTokenValid(token));
	}

}
