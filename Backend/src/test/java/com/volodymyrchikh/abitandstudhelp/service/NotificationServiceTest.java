package com.volodymyrchikh.abitandstudhelp.service;

import com.querydsl.core.types.Predicate;
import com.volodymyrchikh.abitandstudhelp.domain.Notification;
import com.volodymyrchikh.abitandstudhelp.dto.NotificationResponse;
import com.volodymyrchikh.abitandstudhelp.mapper.NotificationMapper;
import com.volodymyrchikh.abitandstudhelp.repository.NotificationRepository;
import com.volodymyrchikh.abitandstudhelp.repository.UserRepository;
import com.volodymyrchikh.abitandstudhelp.security.AuthorizationService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationServiceTest {

    private final NotificationRepository notificationRepository = mock(NotificationRepository.class);
    private final NotificationMapper notificationMapper = mock(NotificationMapper.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final AuthorizationService authorizationService = mock(AuthorizationService.class);
    private final NotificationService notificationService = new NotificationService(
            notificationRepository,
            notificationMapper,
            userRepository,
            authorizationService
    );

    @Test
    void regularUsersOnlyListTheirOwnNotifications() {
        Pageable pageable = PageRequest.of(0, 10);
        Notification notification = Notification.builder().id(1L).build();
        NotificationResponse response = NotificationResponse.builder().id(1L).build();
        when(authorizationService.isCurrentUserAdmin()).thenReturn(false);
        when(authorizationService.currentUserEmail()).thenReturn(Optional.of("user@example.com"));
        when(notificationRepository.findAllByUserEmail("user@example.com", pageable))
                .thenReturn(new PageImpl<>(List.of(notification), pageable, 1));
        when(notificationMapper.toResponse(notification)).thenReturn(response);

        Page<NotificationResponse> result = notificationService.getAll(pageable, null);

        assertEquals(List.of(response), result.getContent());
        verify(notificationRepository).findAllByUserEmail("user@example.com", pageable);
        verify(notificationRepository, never()).findAll((Predicate) null, pageable);
    }

    @Test
    void adminsCanListNotificationsWithExistingFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Predicate filter = mock(Predicate.class);
        Notification notification = Notification.builder().id(1L).build();
        NotificationResponse response = NotificationResponse.builder().id(1L).build();
        when(authorizationService.isCurrentUserAdmin()).thenReturn(true);
        when(notificationRepository.findAll(filter, pageable))
                .thenReturn(new PageImpl<>(List.of(notification), pageable, 1));
        when(notificationMapper.toResponse(notification)).thenReturn(response);

        Page<NotificationResponse> result = notificationService.getAll(pageable, filter);

        assertEquals(List.of(response), result.getContent());
        verify(notificationRepository).findAll(filter, pageable);
        verify(notificationRepository, never()).findAllByUserEmail("user@example.com", pageable);
    }
}
