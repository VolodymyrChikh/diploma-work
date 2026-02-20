package com.volodymyrchikh.abitandstudhelp.controller;

import com.querydsl.core.types.Predicate;
import com.volodymyrchikh.abitandstudhelp.domain.Notification;
import com.volodymyrchikh.abitandstudhelp.dto.NotificationRequest;
import com.volodymyrchikh.abitandstudhelp.dto.NotificationResponse;
import com.volodymyrchikh.abitandstudhelp.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public NotificationResponse create(@RequestBody @Valid NotificationRequest request) {
        return notificationService.create(request);
    }

    @GetMapping("/{notificationId}")
    public NotificationResponse getById(@PathVariable Long notificationId) {
        return notificationService.getById(notificationId);
    }

    @GetMapping
    public Page<NotificationResponse> getAll(@PageableDefault Pageable pageable,
                                             @QuerydslPredicate(root = Notification.class) Predicate filter) {
        return notificationService.getAll(pageable, filter);
    }

    @PutMapping("/{notificationId}")
    public NotificationResponse update(@PathVariable Long notificationId,
                                       @RequestBody @Valid NotificationRequest request) {
        return notificationService.update(notificationId, request);
    }

    @DeleteMapping("/{notificationId}")
    public void delete(@PathVariable Long notificationId) {
        notificationService.delete(notificationId);
    }
}
