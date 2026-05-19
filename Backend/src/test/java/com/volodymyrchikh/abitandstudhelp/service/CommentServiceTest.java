package com.volodymyrchikh.abitandstudhelp.service;

import com.volodymyrchikh.abitandstudhelp.domain.User;
import com.volodymyrchikh.abitandstudhelp.dto.CommentRequest;
import com.volodymyrchikh.abitandstudhelp.exception.CommentNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.PostNotFoundException;
import com.volodymyrchikh.abitandstudhelp.mapper.CommentMapper;
import com.volodymyrchikh.abitandstudhelp.repository.CommentRepository;
import com.volodymyrchikh.abitandstudhelp.repository.PostRepository;
import com.volodymyrchikh.abitandstudhelp.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommentServiceTest {

    private final CommentRepository commentRepository = mock(CommentRepository.class);
    private final CommentMapper commentMapper = mock(CommentMapper.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final PostRepository postRepository = mock(PostRepository.class);
    private final CommentDetectorService commentDetectorService = mock(CommentDetectorService.class);
    private final CommentAsyncService commentAsyncService = mock(CommentAsyncService.class);
    private final NotificationService notificationService = mock(NotificationService.class);
    private final CommentService commentService = new CommentService(
            commentRepository,
            commentMapper,
            userRepository,
            postRepository,
            commentDetectorService,
            commentAsyncService,
            notificationService
    );

    @Test
    void getByIdUsesUkrainianMessageWhenCommentMissing() {
        when(commentRepository.findById(404L)).thenReturn(Optional.empty());

        CommentNotFoundException exception = assertThrows(
                CommentNotFoundException.class,
                () -> commentService.getById(404L)
        );

        assertEquals("Коментар не знайдено", exception.getMessage());
        assertEquals(404L, exception.getCommentId());
    }

    @Test
    void createUsesUkrainianMessageWhenPostMissing() {
        CommentRequest request = new CommentRequest();
        request.setContent("Питання");
        request.setPostId(404L);
        request.setUserId(7L);

        when(userRepository.findById(7L)).thenReturn(Optional.of(User.builder()
                .id(7L)
                .build()));
        when(postRepository.findById(404L)).thenReturn(Optional.empty());

        PostNotFoundException exception = assertThrows(
                PostNotFoundException.class,
                () -> commentService.create(request)
        );

        assertEquals("Допис не знайдено", exception.getMessage());
        assertEquals(404L, exception.getPostId());
    }
}
