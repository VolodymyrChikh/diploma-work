package com.volodymyrchikh.abitandstudhelp.security.auth;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    private final JwtService jwtService = mock(JwtService.class);
    private final UserDetailsService userDetailsService = mock(UserDetailsService.class);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void continuesAsAnonymousWhenBearerTokenCannotBeParsed() throws Exception {
        when(jwtService.extractUserEmail("invalid-token")).thenThrow(new JwtException("Invalid token"));

        AtomicBoolean chainCalled = new AtomicBoolean(false);
        MockHttpServletRequest request = requestWithBearerToken("invalid-token");

        filter.doFilter(request, new MockHttpServletResponse(), markChainCalled(chainCalled));

        assertTrue(chainCalled.get());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void continuesAsAnonymousWhenBearerTokenDoesNotValidate() throws Exception {
        UserDetails userDetails = new User("user@example.com", "password", List.of());
        when(jwtService.extractUserEmail("expired-token")).thenReturn("user@example.com");
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("expired-token", userDetails)).thenReturn(false);

        AtomicBoolean chainCalled = new AtomicBoolean(false);
        MockHttpServletRequest request = requestWithBearerToken("expired-token");

        filter.doFilter(request, new MockHttpServletResponse(), markChainCalled(chainCalled));

        assertTrue(chainCalled.get());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private MockHttpServletRequest requestWithBearerToken(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        return request;
    }

    private FilterChain markChainCalled(AtomicBoolean chainCalled) {
        return (request, response) -> chainCalled.set(true);
    }
}
