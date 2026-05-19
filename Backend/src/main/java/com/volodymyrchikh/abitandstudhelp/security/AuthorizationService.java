package com.volodymyrchikh.abitandstudhelp.security;

import com.volodymyrchikh.abitandstudhelp.domain.Comment;
import com.volodymyrchikh.abitandstudhelp.domain.Notification;
import com.volodymyrchikh.abitandstudhelp.domain.Post;
import com.volodymyrchikh.abitandstudhelp.domain.User;
import com.volodymyrchikh.abitandstudhelp.repository.CommentRepository;
import com.volodymyrchikh.abitandstudhelp.repository.NotificationRepository;
import com.volodymyrchikh.abitandstudhelp.repository.PostRepository;
import com.volodymyrchikh.abitandstudhelp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("authorizationService")
@RequiredArgsConstructor
public class AuthorizationService {

    private static final String ADMIN_AUTHORITY = "ROLE_ADMIN";
    private static final String ANONYMOUS_PRINCIPAL = "anonymousUser";

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;

    public boolean isCurrentUserOrAdmin(Long userId) {
        if (userId == null) {
            return false;
        }

        Authentication authentication = currentAuthentication();
        if (isAdmin(authentication)) {
            return true;
        }

        return currentUser(authentication)
                .map(User::getId)
                .map(userId::equals)
                .orElse(false);
    }

    public boolean canManagePost(Long postId) {
        if (postId == null) {
            return false;
        }

        Authentication authentication = currentAuthentication();
        if (isAdmin(authentication)) {
            return true;
        }

        Optional<Long> currentUserId = currentUser(authentication).map(User::getId);
        return currentUserId.flatMap(userId -> postRepository.findById(postId)
                        .map(Post::getUser)
                        .map(User::getId)
                        .map(userId::equals))
                .orElse(false);
    }

    public boolean canManageComment(Long commentId) {
        if (commentId == null) {
            return false;
        }

        Authentication authentication = currentAuthentication();
        if (isAdmin(authentication)) {
            return true;
        }

        Optional<Long> currentUserId = currentUser(authentication).map(User::getId);
        return currentUserId.flatMap(userId -> commentRepository.findById(commentId)
                        .map(Comment::getUser)
                        .map(User::getId)
                        .map(userId::equals))
                .orElse(false);
    }

    public boolean canManageNotification(Long notificationId) {
        if (notificationId == null) {
            return false;
        }

        Authentication authentication = currentAuthentication();
        if (isAdmin(authentication)) {
            return true;
        }

        Optional<Long> currentUserId = currentUser(authentication).map(User::getId);
        return currentUserId.flatMap(userId -> notificationRepository.findById(notificationId)
                        .map(Notification::getUser)
                        .map(User::getId)
                        .map(userId::equals))
                .orElse(false);
    }

    public boolean isCurrentUserAdmin() {
        return isAdmin(currentAuthentication());
    }

    public Optional<String> currentUserEmail() {
        Authentication authentication = currentAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        String email = authentication.getName();
        if (email == null || email.isBlank() || ANONYMOUS_PRINCIPAL.equals(email)) {
            return Optional.empty();
        }

        return Optional.of(email);
    }

    private Authentication currentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(ADMIN_AUTHORITY::equals);
    }

    private Optional<User> currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        return currentUserEmail()
                .flatMap(userRepository::findByEmail);
    }
}
