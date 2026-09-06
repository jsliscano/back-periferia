package com.prueba.periferia.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.prueba.periferia.security.JwtAuthenticationFilter;

import io.swagger.v3.oas.models.OpenAPI;

class ConfigUnitTest {

	@Test
	void securityConfig_shouldProvidePasswordEncoder() {
		SecurityConfig config = new SecurityConfig(mock(JwtAuthenticationFilter.class));
		PasswordEncoder encoder = config.passwordEncoder();

		String hash = encoder.encode("secret");
		assertTrue(encoder.matches("secret", hash));
	}

	@Test
	void corsConfig_shouldAllowAllOriginPatterns() {
		CorsConfig config = new CorsConfig();
		CorsConfigurationSource source = config.corsConfigurationSource();

		assertTrue(source instanceof UrlBasedCorsConfigurationSource);
		CorsConfiguration cors = ((UrlBasedCorsConfigurationSource) source)
				.getCorsConfigurations()
				.get("/**");

		assertNotNull(cors);
		assertTrue(cors.getAllowedOriginPatterns().contains("*"));
		assertTrue(cors.getAllowedMethods().contains("POST"));
	}

	@Test
	void openApiConfig_shouldExposeBearerScheme() {
		OpenApiConfig config = new OpenApiConfig();
		OpenAPI openAPI = config.periferiaOpenAPI();

		assertEquals("Periferia API", openAPI.getInfo().getTitle());
		assertNotNull(openAPI.getComponents().getSecuritySchemes().get("bearerAuth"));
	}

}
