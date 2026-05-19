package com.volodymyrchikh.abitandstudhelp.security;

import com.volodymyrchikh.abitandstudhelp.domain.Comment;
import com.volodymyrchikh.abitandstudhelp.domain.Notification;
import com.volodymyrchikh.abitandstudhelp.domain.Post;
import com.volodymyrchikh.abitandstudhelp.domain.User;
import com.volodymyrchikh.abitandstudhelp.repository.CommentRepository;
import com.volodymyrchikh.abitandstudhelp.repository.NotificationRepository;
import com.volodymyrchikh.abitandstudhelp.repository.PostRepository;
import com.volodymyrchikh.abitandstudhelp.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthorizationServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PostRepository postRepository = mock(PostRepository.class);
    private final CommentRepository commentRepository = mock(CommentRepository.class);
    private final NotificationRepository notificationRepository = mock(NotificationRepository.class);
    private final AuthorizationService authorizationService = new AuthorizationService(
            userRepository,
            postRepository,
            commentRepository,
            notificationRepository
    );

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void allowsCurrentUserIdForAuthenticatedUser() {
        authenticate("owner@example.com", "ROLE_USER");
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(user(1L)));

        assertTrue(authorizationService.isCurrentUserOrAdmin(1L));
    }

    @Test
    void rejectsDifferentUserIdForAuthenticatedUser() {
        authenticate("owner@example.com", "ROLE_USER");
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(user(1L)));

        assertFalse(authorizationService.isCurrentUserOrAdmin(2L));
    }

    @Test
    void allowsAdminForAnyUserId() {
        authenticate("admin@example.com", "ROLE_ADMIN");

        assertTrue(authorizationService.isCurrentUserOrAdmin(99L));
    }

    @Test
    void allowsPostOwnerToManagePost() {
        authenticate("owner@example.com", "ROLE_USER");
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(user(1L)));
        when(postRepository.findById(10L)).thenReturn(Optional.of(postOwnedBy(1L)));

        assertTrue(authorizationService.canManagePost(10L));
    }

    @Test
    void rejectsNonOwnerFromManagingPost() {
        authenticate("owner@example.com", "ROLE_USER");
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(user(1L)));
        when(postRepository.findById(10L)).thenReturn(Optional.of(postOwnedBy(2L)));

        assertFalse(authorizationService.canManagePost(10L));
    }

    @Test
    void allowsCommentOwnerToManageComment() {
        authenticate("owner@example.com", "ROLE_USER");
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(user(1L)));
        when(commentRepository.findById(20L)).thenReturn(Optional.of(commentOwnedBy(1L)));

        assertTrue(authorizationService.canManageComment(20L));
    }

    @Test
    void allowsNotificationOwnerToManageNotification() {
        authenticate("owner@example.com", "ROLE_USER");
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(user(1L)));
        when(notificationRepository.findById(30L)).thenReturn(Optional.of(notificationOwnedBy(1L)));

        assertTrue(authorizationService.canManageNotification(30L));
    }

    @Test
    void rejectsNonOwnerFromManagingNotification() {
        authenticate("owner@example.com", "ROLE_USER");
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(user(1L)));
        when(notificationRepository.findById(30L)).thenReturn(Optional.of(notificationOwnedBy(2L)));

        assertFalse(authorizationService.canManageNotification(30L));
    }

    @Test
    void rejectsAnonymousUsers() {
        SecurityContextHolder.clearContext();

        assertFalse(authorizationService.isCurrentUserOrAdmin(1L));
        assertFalse(authorizationService.canManagePost(10L));
        assertFalse(authorizationService.canManageComment(20L));
        assertFalse(authorizationService.canManageNotification(30L));
    }

    private void authenticate(String username, String authority) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(new SimpleGrantedAuthority(authority))
                )
        );
    }

    private User user(Long id) {
        return User.builder()
                .id(id)
                .email("user-%s@example.com".formatted(id))
                .build();
    }

    private Post postOwnedBy(Long userId) {
        return Post.builder()
                .id(10L)
                .user(user(userId))
                .build();
    }

    private Comment commentOwnedBy(Long userId) {
        return Comment.builder()
                .id(20L)
                .user(user(userId))
                .build();
    }

    private Notification notificationOwnedBy(Long userId) {
        return Notification.builder()
                .id(30L)
                .user(user(userId))
                .build();
    }
}
