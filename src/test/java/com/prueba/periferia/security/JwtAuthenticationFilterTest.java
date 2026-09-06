package com.prueba.periferia.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

	@Mock
	private JwtService jwtService;

	@InjectMocks
	private JwtAuthenticationFilter filter;

	@BeforeEach
	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void shouldContinue_whenAuthorizationHeaderIsMissing() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		filter.doFilterInternal(request, response, chain);

		assertNull(SecurityContextHolder.getContext().getAuthentication());
		verify(jwtService, never()).isTokenValid(any());
	}

	@Test
	void shouldContinue_whenHeaderIsNotBearer() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(HttpHeaders.AUTHORIZATION, "Basic abc");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		filter.doFilterInternal(request, response, chain);

		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	void shouldAuthenticate_whenTokenIsValid() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid-token");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		when(jwtService.isTokenValid("valid-token")).thenReturn(true);
		when(jwtService.extractEmail("valid-token")).thenReturn("john@example.com");
		when(jwtService.extractUserId("valid-token")).thenReturn(1L);

		filter.doFilterInternal(request, response, chain);

		assertEquals("john@example.com", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		assertEquals(1L, SecurityContextHolder.getContext().getAuthentication().getDetails());
	}

	@Test
	void shouldClearContext_whenTokenIsInvalid() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer bad-token");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();

		when(jwtService.isTokenValid("bad-token")).thenThrow(new RuntimeException("invalid"));

		filter.doFilterInternal(request, response, chain);

		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

}
