package com.volodymyrchikh.abitandstudhelp.controller;

import com.querydsl.core.types.Predicate;
import com.volodymyrchikh.abitandstudhelp.domain.Comment;
import com.volodymyrchikh.abitandstudhelp.domain.Post;
import com.volodymyrchikh.abitandstudhelp.dto.CommentRequest;
import com.volodymyrchikh.abitandstudhelp.dto.CommentResponse;
import com.volodymyrchikh.abitandstudhelp.dto.PostResponse;
import com.volodymyrchikh.abitandstudhelp.dto.UpdateCommentRequest;
import com.volodymyrchikh.abitandstudhelp.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @MessageMapping("/chat")    
    @SendTo("/topic/messages")
    public CommentResponse send(CommentRequest message) {
        return commentService.create(message);
    }

    @PostMapping
    public CommentResponse create(@RequestBody @Valid CommentRequest commentRequest) {
        return commentService.create(commentRequest);
    }

    @GetMapping("/{id}")
    public CommentResponse getById(@PathVariable Long id) {
        return commentService.getById(id);
    }

    @GetMapping
    public Page<CommentResponse> getAll(@PageableDefault Pageable pageable,
                                        @QuerydslPredicate(root = Comment.class) Predicate filter) {
        return commentService.getAll(pageable, filter);
    }

    @GetMapping("/post/{postId}")
    public Page<CommentResponse> getAllByPostId(@PathVariable Long postId,
                                                @PageableDefault Pageable pageable,
                                                @QuerydslPredicate(root = Comment.class) Predicate filter) {
        return commentService.getAllByPostId(postId, pageable, filter);
    }

    @PutMapping("/{id}")
    public CommentResponse update(@PathVariable Long id, @RequestBody @Valid UpdateCommentRequest commentRequest) {
        return commentService.update(id, commentRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        commentService.delete(id);
    }

    @GetMapping("/last-comments")
    public Map<Long, CommentResponse> getLastCommentsForAllPosts() {
        return commentService.getLastCommentsForAllPosts();
    }

    @GetMapping("/post/{postId}/count")
    public long getCommentsCount(@PathVariable Long postId) {
        return commentService.countByPostId(postId);
    }
}
