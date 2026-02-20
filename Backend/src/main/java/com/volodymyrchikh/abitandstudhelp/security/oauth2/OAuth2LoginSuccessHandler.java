package com.volodymyrchikh.abitandstudhelp.security.oauth2;

import com.volodymyrchikh.abitandstudhelp.dto.AuthenticationResponse;
import com.volodymyrchikh.abitandstudhelp.service.AuthenticationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthenticationService authenticationService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");
        String avatarUrl = oAuth2User.getAttribute("picture");

        // Handle GitHub attributes (fallback if google-specific attributes are missing)
        if (email == null) {
            // GitHub sometimes returns email as null if it's private, even with scope.
            // But we check if it is in attributes map.
            // Also handle name mapping.
            if (oAuth2User.getAttributes().containsKey("avatar_url")) {
                avatarUrl = oAuth2User.getAttribute("avatar_url");
            }
            if (oAuth2User.getAttributes().containsKey("name")) {
                String name = oAuth2User.getAttribute("name");
                if (name != null) {
                    String[] parts = name.split(" ", 2);
                    firstName = parts[0];
                    if (parts.length > 1) {
                        lastName = parts[1];
                    }
                }
            } else if (oAuth2User.getAttributes().containsKey("login")) {
                 firstName = oAuth2User.getAttribute("login");
            }
        }

        // If email is still null, we technically can't register them easily in this flow without asking for it.
        // Spring Security OAuth2 Client might populate "email" attribute even for GitHub if properly mapped, but usually it requires an extra API call if private.
        // We will assume email is present or passed.

        if (email == null) {
             response.sendRedirect("http://localhost:3000/login?error=NoEmailProvided");
             return;
        }

        AuthenticationResponse authResponse = authenticationService.authenticateOAuthUser(email, firstName, lastName, avatarUrl);

        String targetUrl = "http://localhost:3000/oauth2/redirect?accessToken=" + authResponse.getToken().getToken()
                + "&refreshToken=" + authResponse.getToken().getRefreshToken();

        response.sendRedirect(targetUrl);
    }
}



