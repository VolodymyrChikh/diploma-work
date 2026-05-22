package com.volodymyrchikh.abitandstudhelp.service;

import com.github.pemistahl.lingua.api.Language;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.volodymyrchikh.abitandstudhelp.common.NotificationType;
import com.volodymyrchikh.abitandstudhelp.domain.*;
import com.volodymyrchikh.abitandstudhelp.dto.CommentRequest;
import com.volodymyrchikh.abitandstudhelp.dto.CommentResponse;
import com.volodymyrchikh.abitandstudhelp.dto.NotificationRequest;
import com.volodymyrchikh.abitandstudhelp.dto.UpdateCommentRequest;
import com.volodymyrchikh.abitandstudhelp.exception.CommentNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.PostNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.UserNotFoundException;
import com.volodymyrchikh.abitandstudhelp.mapper.CommentMapper;
import com.volodymyrchikh.abitandstudhelp.repository.CommentRepository;
import com.volodymyrchikh.abitandstudhelp.repository.PostRepository;
import com.volodymyrchikh.abitandstudhelp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentDetectorService commentDetectorService;
    private final CommentAsyncService commentAsyncService;
    private final NotificationService notificationService;

    public CommentResponse create(CommentRequest commentRequest) {
        User user = userRepository.findById(commentRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Користувача не знайдено", commentRequest.getUserId()));

        Post post = postRepository.findById(commentRequest.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Допис не знайдено", commentRequest.getPostId()));

        Language detectedLanguage = commentDetectorService.detectLanguage(commentRequest.getContent());

        Comment comment = Comment.builder()
                .content(commentRequest.getContent())
                .language(detectedLanguage.toString())
                .user(user)
                .post(post)
                .build();

        Comment savedComment = commentRepository.save(comment);

        if (detectedLanguage.equals(Language.RUSSIAN) ||
                commentDetectorService.containsCurseWords(comment.getContent())) {
            commentAsyncService.scheduleNotification(user.getId());
            commentAsyncService.scheduleCommentDeletion(savedComment.getId());
        }

        return commentMapper.mapToResponse(savedComment);
    }

    public CommentResponse getById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Коментар не знайдено", commentId));

        return commentMapper.mapToResponse(comment);
    }

    public Page<CommentResponse> getAll(Pageable pageable, Predicate filter) {
        return commentRepository.findAll(filter, pageable)
                .map(commentMapper::mapToResponse);
    }

    public Page<CommentResponse> getAllByPostId(Long postId, Pageable pageable, Predicate filter) {
        QComment qComment = QComment.comment;

        BooleanExpression postIdPredicate = qComment.post.id.eq(postId);

        Predicate combinedPredicate = postIdPredicate;
        if (filter != null) {
            combinedPredicate = ExpressionUtils.and(postIdPredicate, filter);
        }

        return commentRepository.findAll(combinedPredicate, pageable)
                .map(commentMapper::mapToResponse);
    }

    public CommentResponse update(Long id, UpdateCommentRequest commentRequest) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Коментар не знайдено", id));

        comment.setContent(commentRequest.getContent());

        Comment updatedComment = commentRepository.save(comment);

        return commentMapper.mapToResponse(updatedComment);
    }

    public void delete(Long commentId) {
        delete(commentId, null, null);
    }

    public void delete(Long commentId, String deletionReason, String notificationTypeValue) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Коментар не знайдено", commentId));

        Long commentOwnerId = comment.getUser() != null ? comment.getUser().getId() : null;
        commentRepository.delete(comment);

        if (deletionReason != null && !deletionReason.isBlank() && commentOwnerId != null) {
            notifyCommentOwnerAboutDeletion(commentOwnerId, deletionReason, notificationTypeValue);
        }
    }

    private void notifyCommentOwnerAboutDeletion(Long commentOwnerId, String deletionReason, String notificationTypeValue) {
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .type(resolveNotificationType(notificationTypeValue).getName())
                .message("Ваш коментар був видалений адміністратором. Причина: " + deletionReason)
                .userId(commentOwnerId)
                .build();
        notificationService.create(notificationRequest);
    }

    private NotificationType resolveNotificationType(String notificationTypeValue) {
        if (notificationTypeValue == null || notificationTypeValue.isBlank()) {
            return NotificationType.ADMIN_MESSAGE;
        }

        try {
            return NotificationType.forValue(notificationTypeValue);
        } catch (IllegalArgumentException ignored) {
            return NotificationType.ADMIN_MESSAGE;
        }
    }

    public void deleteByRules(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Коментар не знайдено", commentId));

        if (comment.getLanguage().equalsIgnoreCase("RUSSIAN") ||
                commentDetectorService.containsCurseWords(comment.getContent())) {
            commentRepository.delete(comment);
        }
    }

    public Map<Long, CommentResponse> getLastCommentsForAllPosts() {
        List<Comment> lastComments = commentRepository.findLastCommentsForAllPosts();
        return lastComments.stream()
                .map(commentMapper::mapToResponse)
                .collect(Collectors.toMap(CommentResponse::getId, Function.identity()));
    }

    public long countByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    public Map<String, Boolean> checkCommentDeletionRules(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Коментар не знайдено", commentId));

        boolean needsDeletion = comment.getLanguage().equalsIgnoreCase("RUSSIAN") ||
                commentDetectorService.containsCurseWords(comment.getContent());

        return Map.of("needsDeletion", needsDeletion);
    }
}
