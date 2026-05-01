package com.volodymyrchikh.abitandstudhelp.controller;

import com.querydsl.core.types.Predicate;
import com.volodymyrchikh.abitandstudhelp.domain.Post;
import com.volodymyrchikh.abitandstudhelp.dto.PostRequest;
import com.volodymyrchikh.abitandstudhelp.dto.PostResponse;
import com.volodymyrchikh.abitandstudhelp.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public PostResponse create(@RequestBody @Valid PostRequest postRequest) {
        return postService.create(postRequest);
    }

    @GetMapping("/slug/{slug}")
    public PostResponse getBySlug(@PathVariable String slug) {
        return postService.getBySlug(slug);
    }

    @GetMapping("/{id}")
    public PostResponse getById(@PathVariable Long id) {
        return postService.getById(id);
    }

    @GetMapping
    public Page<PostResponse> getAll(@PageableDefault Pageable pageable,
                                     @QuerydslPredicate(root = Post.class) Predicate filter) {
        return postService.getAll(pageable, filter);
    }

    @GetMapping("/by-popularity")
    public Page<PostResponse> getAllByPopularity(@PageableDefault Pageable pageable,
                                                 @QuerydslPredicate(root = Post.class) Predicate filter) {
        return postService.getAllByPopularity(pageable, filter);
    }

    @GetMapping("/by-new")
    public Page<PostResponse> getAllByNew(@PageableDefault Pageable pageable,
                                          @QuerydslPredicate(root = Post.class) Predicate filter) {
        return postService.getAllByNew(pageable, filter);
    }

    @GetMapping("/by-old")
    public Page<PostResponse> getAllByOld(@PageableDefault Pageable pageable,
                                          @QuerydslPredicate(root = Post.class) Predicate filter) {
        return postService.getAllByOld(pageable, filter);
    }

    @GetMapping("/by-category-name/{categoryName}")
    public Page<PostResponse> getAllByCategoryName(@PathVariable String categoryName,
                                                   @PageableDefault Pageable pageable,
                                                   @QuerydslPredicate(root = Post.class) Predicate filter) {
        return postService.getAllByCategoryName(categoryName, pageable, filter);
    }

    @PutMapping("/{id}")
    public PostResponse update(@PathVariable Long id, @RequestBody @Valid PostRequest postRequest) {
        return postService.update(id, postRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        postService.delete(id);
    }

    @PostMapping("/{id}/like")
    public PostResponse likePost(@PathVariable Long id, @RequestParam Long userId) {
        return postService.likePost(id, userId);
    }

    @PostMapping("/{id}/unlike")
    public PostResponse unlikePost(@PathVariable Long id, @RequestParam Long userId) {
        return postService.unlikePost(id, userId);
    }

    @GetMapping("/{id}/likes/check")
    public boolean checkIfUserLikedPost(@PathVariable Long id, @RequestParam Long userId) {
        return postService.hasUserLikedPost(id, userId);
    }
}