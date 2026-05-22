package com.volodymyrchikh.abitandstudhelp.service;

import com.querydsl.core.types.Predicate;
import com.volodymyrchikh.abitandstudhelp.domain.Notification;
import com.volodymyrchikh.abitandstudhelp.domain.User;
import com.volodymyrchikh.abitandstudhelp.dto.NotificationRequest;
import com.volodymyrchikh.abitandstudhelp.dto.NotificationResponse;
import com.volodymyrchikh.abitandstudhelp.exception.NotificationNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.UserNotFoundException;
import com.volodymyrchikh.abitandstudhelp.mapper.NotificationMapper;
import com.volodymyrchikh.abitandstudhelp.repository.NotificationRepository;
import com.volodymyrchikh.abitandstudhelp.repository.UserRepository;
import com.volodymyrchikh.abitandstudhelp.security.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    public final NotificationRepository notificationRepository;
    public final NotificationMapper notificationMapper;
    private final UserRepository userRepository;
    private final AuthorizationService authorizationService;

    public NotificationResponse create(NotificationRequest notificationRequest) {
        User user = userRepository.findById(notificationRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException(("User of id=[%s] not found")
                        .formatted(notificationRequest.getUserId()), notificationRequest.getUserId()));

        Notification notification = notificationMapper.toEntity(notificationRequest);

        notification.setUser(user);

        Notification savedNotification = notificationRepository.save(notification);

        return notificationMapper.toResponse(savedNotification);
    }


    public NotificationResponse getById(Long notificationId) {
        return notificationMapper.toResponse(notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification of id=[%s] not found"
                        .formatted(notificationId), notificationId)));
    }

    public Page<NotificationResponse> getAll(Pageable pageable, Predicate filter) {
        String email = authorizationService.currentUserEmail()
                .orElseThrow(() -> new AccessDeniedException("Authenticated user is required"));
        
        // Return only the current user's notifications, regardless of admin status.
        // Admins should not see system-wide notifications in their personal popup.
        return notificationRepository.findAllByUserEmail(email, pageable)
                .map(notificationMapper::toResponse);
    }

    public NotificationResponse update(Long notificationId, NotificationRequest notificationRequest) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification of id=[%s] not found"
                        .formatted(notificationId), notificationId));
        notificationMapper.updateEntity(notificationRequest, notification);
        return notificationMapper.toResponse(notificationRepository.save(notification));
    }

    public void delete(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification of id=[%s] not found"
                        .formatted(notificationId), notificationId));
        notificationRepository.delete(notification);
    }
}
