package com.volodymyrchikh.abitandstudhelp.security;

import com.volodymyrchikh.abitandstudhelp.security.auth.JwtAuthenticationFilter;
import com.volodymyrchikh.abitandstudhelp.security.auth.JwtService;
import com.volodymyrchikh.abitandstudhelp.security.auth.CustomAuthenticationEntryPoint;
import com.volodymyrchikh.abitandstudhelp.mapper.ErrorDetailMapper;
import com.volodymyrchikh.abitandstudhelp.service.UsersDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        SecurityConfigurationTest.PublicContentController.class,
        SecurityConfigurationTest.UserEndpointController.class,
        SecurityConfigurationTest.AdminContentController.class,
        SecurityConfigurationTest.AuthEndpointController.class,
        SecurityConfigurationTest.ErrorEndpointController.class,
        SecurityConfigurationTest.SpaController.class
})
@Import({SecurityConfiguration.class, SecurityConfigurationTest.SecurityTestBeans.class})
class SecurityConfigurationTest {

    private final MockMvc mockMvc;

    @Autowired
    SecurityConfigurationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void allowsAnonymousUsersToReadPublicContent() throws Exception {
        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk());
    }

    @Test
    void allowsLocalNetworkFrontendOrigin() throws Exception {
        mockMvc.perform(get("/posts")
                        .header("Origin", "http://192.168.50.151:3001"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://192.168.50.151:3001"));
    }

    @Test
    void allowsAnonymousUsersToLoadSpaRoutes() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk());
    }

    @Test
    void rejectsAnonymousUsersFromMutatingPosts() throws Exception {
        mockMvc.perform(post("/posts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void allowsAnonymousUsersToRegisterAndAuthenticate() throws Exception {
        mockMvc.perform(post("/auth/register"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/authenticate"))
                .andExpect(status().isOk());
    }

    @Test
    void allowsAnonymousUsersToReadErrorResponses() throws Exception {
        mockMvc.perform(get("/error"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void allowsAuthenticatedUsersToMutatePosts() throws Exception {
        mockMvc.perform(post("/posts"))
                .andExpect(status().isOk());
    }

    @Test
    void rejectsAnonymousUsersFromCurrentUserEndpoint() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void rejectsRegularUsersFromAdminContentMutations() throws Exception {
        mockMvc.perform(post("/categories"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void returnsUkrainianProblemDetailForForbiddenRequests() throws Exception {
        mockMvc.perform(post("/categories").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Доступ заборонено"))
                .andExpect(jsonPath("$.detail").value("Недостатньо прав для цієї дії"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void allowsAdminsToMutateAdminContent() throws Exception {
        mockMvc.perform(post("/categories"))
                .andExpect(status().isOk());
    }

    @RestController
    static class PublicContentController {
        @GetMapping("/posts")
        ResponseEntity<Void> readPosts() {
            return ResponseEntity.ok().build();
        }

        @PostMapping("/posts")
        ResponseEntity<Void> createPost() {
            return ResponseEntity.ok().build();
        }
    }

    @RestController
    static class UserEndpointController {
        @GetMapping("/users/me")
        ResponseEntity<Void> currentUser() {
            return ResponseEntity.ok().build();
        }
    }

    @RestController
    static class AdminContentController {
        @PostMapping("/categories")
        ResponseEntity<Void> createCategory() {
            return ResponseEntity.ok().build();
        }
    }

    @RestController
    static class AuthEndpointController {
        @PostMapping("/auth/register")
        ResponseEntity<Void> register() {
            return ResponseEntity.ok().build();
        }

        @PostMapping("/auth/authenticate")
        ResponseEntity<Void> authenticate() {
            return ResponseEntity.ok().build();
        }
    }

    @RestController
    static class ErrorEndpointController {
        @GetMapping("/error")
        ResponseEntity<Void> error() {
            return ResponseEntity.ok().build();
        }
    }

    @Controller
    static class SpaController {
        @GetMapping("/profile")
        ResponseEntity<Void> profile() {
            return ResponseEntity.ok().build();
        }
    }

    @TestConfiguration
    static class SecurityTestBeans {
        @Bean
        PublicContentController publicContentController() {
            return new PublicContentController();
        }

        @Bean
        UserEndpointController userEndpointController() {
            return new UserEndpointController();
        }

        @Bean
        AdminContentController adminContentController() {
            return new AdminContentController();
        }

        @Bean
        AuthEndpointController authEndpointController() {
            return new AuthEndpointController();
        }

        @Bean
        ErrorEndpointController errorEndpointController() {
            return new ErrorEndpointController();
        }

        @Bean
        SpaController spaController() {
            return new SpaController();
        }

        @Bean
        UsersDetailsService usersDetailsService() {
            return mock(UsersDetailsService.class);
        }

        @Bean
        AuthenticationProvider authenticationProvider() {
            return mock(AuthenticationProvider.class);
        }

        @Bean
        ErrorDetailMapper errorDetailMapper() {
            return mock(ErrorDetailMapper.class);
        }

        @Bean
        AuthenticationEntryPoint authenticationEntryPoint() {
            return new CustomAuthenticationEntryPoint();
        }

        @Bean
        JwtAuthenticationFilter jwtAuthenticationFilter() {
            return new PassThroughJwtAuthenticationFilter(mock(JwtService.class), mock(UsersDetailsService.class));
        }
    }

    static class PassThroughJwtAuthenticationFilter extends JwtAuthenticationFilter {

        PassThroughJwtAuthenticationFilter(JwtService jwtService, UsersDetailsService userDetailsService) {
            super(jwtService, userDetailsService);
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            filterChain.doFilter(request, response);
        }
    }
}
