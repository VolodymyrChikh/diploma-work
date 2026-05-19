package com.volodymyrchikh.abitandstudhelp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volodymyrchikh.abitandstudhelp.controller.CommentController;
import com.volodymyrchikh.abitandstudhelp.controller.NotificationController;
import com.volodymyrchikh.abitandstudhelp.controller.PostController;
import com.volodymyrchikh.abitandstudhelp.controller.UserController;
import com.volodymyrchikh.abitandstudhelp.dto.CommentRequest;
import com.volodymyrchikh.abitandstudhelp.dto.NotificationRequest;
import com.volodymyrchikh.abitandstudhelp.dto.PostRequest;
import com.volodymyrchikh.abitandstudhelp.dto.UpdateCommentRequest;
import com.volodymyrchikh.abitandstudhelp.dto.UpdateUserRequest;
import com.volodymyrchikh.abitandstudhelp.mapper.ErrorDetailMapper;
import com.volodymyrchikh.abitandstudhelp.security.auth.CustomAuthenticationEntryPoint;
import com.volodymyrchikh.abitandstudhelp.security.auth.JwtAuthenticationFilter;
import com.volodymyrchikh.abitandstudhelp.security.auth.JwtService;
import com.volodymyrchikh.abitandstudhelp.service.CommentService;
import com.volodymyrchikh.abitandstudhelp.service.NotificationService;
import com.volodymyrchikh.abitandstudhelp.service.PostService;
import com.volodymyrchikh.abitandstudhelp.service.UserService;
import com.volodymyrchikh.abitandstudhelp.service.UsersDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        PostController.class,
        CommentController.class,
        NotificationController.class,
        UserController.class
})
@Import({SecurityConfiguration.class, OwnershipAuthorizationTest.SecurityTestBeans.class})
class OwnershipAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OwnershipPolicyProbe authorizationService;

    @BeforeEach
    void resetPolicy() {
        authorizationService.reset();
    }

    @Test
    @WithMockUser(roles = "USER")
    void rejectsCreatingPostForAnotherUser() throws Exception {
        authorizationService.denyCurrentUserId(2L);

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest(2L))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void allowsCreatingPostForCurrentUser() throws Exception {
        authorizationService.allowCurrentUserId(1L);

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest(1L))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void rejectsUpdatingAnotherUsersPost() throws Exception {
        authorizationService.denyPost(10L);

        mockMvc.perform(put("/posts/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest(1L))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void rejectsDeletingAnotherUsersPost() throws Exception {
        authorizationService.denyPost(10L);

        mockMvc.perform(delete("/posts/10"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void rejectsLikingAsAnotherUser() throws Exception {
        authorizationService.denyCurrentUserId(2L);

        mockMvc.perform(post("/posts/10/like")
                        .param("userId", "2"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void rejectsCheckingLikesAsAnotherUser() throws Exception {
        authorizationService.denyCurrentUserId(2L);

        mockMvc.perform(get("/posts/10/likes/check")
                        .param("userId", "2"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void rejectsCreatingCommentForAnotherUser() throws Exception {
        authorizationService.denyCurrentUserId(2L);

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest(2L))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void rejectsUpdatingAnotherUsersComment() throws Exception {
        authorizationService.denyComment(44L);

        mockMvc.perform(put("/comments/44")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommentRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void rejectsDeletingAnotherUsersComment() throws Exception {
        authorizationService.denyComment(44L);

        mockMvc.perform(delete("/comments/44"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void rejectsCreatingNotificationForAnotherUser() throws Exception {
        authorizationService.denyCurrentUserId(2L);

        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationRequest(2L))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void rejectsReadingAnotherUsersNotification() throws Exception {
        authorizationService.denyNotification(77L);

        mockMvc.perform(get("/notifications/77"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void rejectsUpdatingAnotherUsersNotification() throws Exception {
        authorizationService.denyNotification(77L);

        mockMvc.perform(put("/notifications/77")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationRequest(1L))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void rejectsDeletingAnotherUsersNotification() throws Exception {
        authorizationService.denyNotification(77L);

        mockMvc.perform(delete("/notifications/77"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void rejectsReadingAnotherUsersProfile() throws Exception {
        authorizationService.denyCurrentUserId(2L);

        mockMvc.perform(get("/users/2"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void rejectsUpdatingAnotherUsersProfile() throws Exception {
        authorizationService.denyCurrentUserId(2L);

        mockMvc.perform(put("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void allowsDeletingOwnAvatar() throws Exception {
        authorizationService.allowCurrentUserId(1L);

        mockMvc.perform(delete("/users/1/avatar"))
                .andExpect(status().isOk());
    }

    @TestConfiguration
    static class SecurityTestBeans {
        @Bean
        PostService postService() {
            return mock(PostService.class);
        }

        @Bean
        CommentService commentService() {
            return mock(CommentService.class);
        }

        @Bean
        NotificationService notificationService() {
            return mock(NotificationService.class);
        }

        @Bean
        UserService userService() {
            return mock(UserService.class);
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

        @Bean("authorizationService")
        OwnershipPolicyProbe authorizationService() {
            return new OwnershipPolicyProbe();
        }
    }

    static class OwnershipPolicyProbe {
        private final Set<Long> allowedCurrentUserIds = new HashSet<>();
        private final Set<Long> deniedCurrentUserIds = new HashSet<>();
        private final Set<Long> allowedPosts = new HashSet<>();
        private final Set<Long> deniedPosts = new HashSet<>();
        private final Set<Long> allowedComments = new HashSet<>();
        private final Set<Long> deniedComments = new HashSet<>();
        private final Set<Long> allowedNotifications = new HashSet<>();
        private final Set<Long> deniedNotifications = new HashSet<>();

        void reset() {
            allowedCurrentUserIds.clear();
            deniedCurrentUserIds.clear();
            allowedPosts.clear();
            deniedPosts.clear();
            allowedComments.clear();
            deniedComments.clear();
            allowedNotifications.clear();
            deniedNotifications.clear();
        }

        void allowCurrentUserId(Long userId) {
            allowedCurrentUserIds.add(userId);
        }

        void denyCurrentUserId(Long userId) {
            deniedCurrentUserIds.add(userId);
        }

        void denyPost(Long postId) {
            deniedPosts.add(postId);
        }

        void denyComment(Long commentId) {
            deniedComments.add(commentId);
        }

        void denyNotification(Long notificationId) {
            deniedNotifications.add(notificationId);
        }

        public boolean isCurrentUserOrAdmin(Long userId) {
            if (deniedCurrentUserIds.contains(userId)) {
                return false;
            }
            return allowedCurrentUserIds.isEmpty() || allowedCurrentUserIds.contains(userId);
        }

        public boolean canManagePost(Long postId) {
            return !deniedPosts.contains(postId) && (allowedPosts.isEmpty() || allowedPosts.contains(postId));
        }

        public boolean canManageComment(Long commentId) {
            return !deniedComments.contains(commentId) && (allowedComments.isEmpty() || allowedComments.contains(commentId));
        }

        public boolean canManageNotification(Long notificationId) {
            return !deniedNotifications.contains(notificationId)
                    && (allowedNotifications.isEmpty() || allowedNotifications.contains(notificationId));
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

    private PostRequest postRequest(Long userId) {
        PostRequest request = new PostRequest();
        request.setTitle("Ownership check");
        request.setCategoryId(1L);
        request.setUserId(userId);
        return request;
    }

    private CommentRequest commentRequest(Long userId) {
        CommentRequest request = new CommentRequest();
        request.setContent("Ownership check");
        request.setPostId(1L);
        request.setUserId(userId);
        return request;
    }

    private UpdateCommentRequest updateCommentRequest() {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setContent("Updated comment");
        return request;
    }

    private NotificationRequest notificationRequest(Long userId) {
        return NotificationRequest.builder()
                .type("INFO")
                .message("Ownership check")
                .isRead(false)
                .userId(userId)
                .build();
    }

    private UpdateUserRequest updateUserRequest() {
        return UpdateUserRequest.builder()
                .firstName("Updated")
                .lastName("User")
                .email("updated@example.com")
                .build();
    }
}
