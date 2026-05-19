package com.volodymyrchikh.abitandstudhelp.service;

import com.volodymyrchikh.abitandstudhelp.common.NotificationType;
import com.volodymyrchikh.abitandstudhelp.dto.NotificationRequest;
import com.volodymyrchikh.abitandstudhelp.exception.CommentNotFoundException;
import com.volodymyrchikh.abitandstudhelp.domain.Comment;
import com.volodymyrchikh.abitandstudhelp.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CommentAsyncService {

    private final CommentRepository commentRepository;

    private final NotificationService notificationService;

    @Value("${comment.deletion.delay:5}") // 24 hours in seconds
    private long commentDeletionDelay;

    @Async
    public void scheduleNotification(Long userId) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .type(NotificationType.INAPPROPRIATE_CONTENT.getName())
                .message("Ваш коментар буде видалений автоматично через 1 добу або адміністратором через порушення правил спільноти.")
                .userId(userId)
                .build();
        notificationService.create(notificationRequest);
    }

    @Async
    public void scheduleCommentDeletion(Long commentId) {
        try {
            TimeUnit.SECONDS.sleep(commentDeletionDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Коментар не знайдено", commentId));
        commentRepository.delete(comment);
    }
}
