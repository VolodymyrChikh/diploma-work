package com.volodymyrchikh.abitandstudhelp.security.auth;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomAuthenticationEntryPointTest {

    private final CustomAuthenticationEntryPoint entryPoint = new CustomAuthenticationEntryPoint();

    @Test
    void unauthorizedResponseUsesHumanUkrainianMessage() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/users/me");
        MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(request, response, new BadCredentialsException("Full authentication is required"));

        String body = response.getContentAsString();
        assertEquals(401, response.getStatus());
        assertTrue(body.contains("Потрібна авторизація"));
        assertTrue(body.contains("Увійдіть в акаунт, щоб продовжити."));
        assertFalse(body.contains("Full authentication is required"));
    }
}
