package com.volodymyrchikh.abitandstudhelp.service;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.volodymyrchikh.abitandstudhelp.domain.Category;
import com.volodymyrchikh.abitandstudhelp.domain.Post;
import com.volodymyrchikh.abitandstudhelp.domain.QPost;
import com.volodymyrchikh.abitandstudhelp.domain.User;
import com.volodymyrchikh.abitandstudhelp.dto.PostRequest;
import com.volodymyrchikh.abitandstudhelp.dto.PostResponse;
import com.volodymyrchikh.abitandstudhelp.exception.CategoryNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.PostNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.UserNotFoundException;
import com.volodymyrchikh.abitandstudhelp.mapper.PostMapper;
import com.volodymyrchikh.abitandstudhelp.repository.CategoryRepository;
import com.volodymyrchikh.abitandstudhelp.repository.PostRepository;
import com.volodymyrchikh.abitandstudhelp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import com.volodymyrchikh.abitandstudhelp.utils.SlugUtils;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public PostResponse create(PostRequest postRequest) {
        Category category = categoryRepository.findById(postRequest.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found", postRequest.getCategoryId()));

        User user = userRepository.findById(postRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found", postRequest.getUserId()));

        // 1. Генеруємо базу для слага з титулу
        String baseSlug = SlugUtils.toSlug(postRequest.getTitle());
        System.out.println("Base Slug: " + baseSlug);
        Post post = Post.builder()
                .title(postRequest.getTitle())
                .likes(postRequest.getLikes())
                .isAnonymous(postRequest.getIsAnonymous())
                .category(category)
                .user(user)
                .slug(generateUniqueSlug(baseSlug)) // 2. Встановлюємо унікальний slug
                .build();

        return postMapper.mapToResponse(postRepository.save(post));
    }

    // Новий метод для отримання за слагом
    public PostResponse getBySlug(String slug) {
        return postRepository.findBySlug(slug)
                .map(postMapper::mapToResponse)
                .orElseThrow(() -> new PostNotFoundException("Post with slug=[%s] not found".formatted(slug), 0L));
    }

    // Рекурсивна або циклічна перевірка унікальності
    private String generateUniqueSlug(String baseSlug) {
        String slug = baseSlug;
        int count = 1;
        while (postRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + count;
            count++;
        }
        return slug;
    }

    public PostResponse getById(Long postId){
        return postMapper.mapToResponse(postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post of id=[%s] not found".formatted(postId), postId)));
    }

    public Page<PostResponse> getAll(Pageable pageable, Predicate filter) {
        return postRepository.findAll(filter, pageable)
                .map(postMapper::mapToResponse);
    }

    public Page<PostResponse> getAllByPopularity(Pageable pageable, Predicate filter) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "likes")
        );
        return postRepository.findAll(filter, sortedPageable)
                .map(postMapper::mapToResponse);
    }

    public Page<PostResponse> getAllByNew(Pageable pageable, Predicate filter) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return postRepository.findAll(filter, sortedPageable)
                .map(postMapper::mapToResponse);
    }

    public Page<PostResponse> getAllByOld(Pageable pageable, Predicate filter) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "createdAt")
        );
        return postRepository.findAll(filter, sortedPageable)
                .map(postMapper::mapToResponse);
    }

    public Page<PostResponse> getAllByCategoryName(String categoryName, Pageable pageable, Predicate filter) {
        QPost post = QPost.post;
        BooleanExpression categoryPredicate = post.category.name.equalsIgnoreCase(categoryName);

        Predicate combinedPredicate;
        if (filter != null) {
            combinedPredicate = categoryPredicate.and(filter);
        } else {
            combinedPredicate = categoryPredicate;
        }

        Page<Post> postsPage = postRepository.findAll(combinedPredicate, pageable);
        return postsPage.map(postMapper::mapToResponse);
    }

    public PostResponse update(Long postId, PostRequest postRequest) {
        Post postToUpdate = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post of id=[%s] not found".formatted(postId), postId));
        postMapper.updatePostFromRequest(postRequest, postToUpdate);
        return postMapper.mapToResponse(postRepository.save(postToUpdate));
    }

    public void delete(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post of id=[%s] not found".formatted(postId), postId));
        postRepository.delete(post);
    }

    public PostResponse likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post of id=[%s] not found".formatted(postId), postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id=[%s] not found".formatted(userId), userId));

        post.addLike(user);
        return postMapper.mapToResponse(postRepository.save(post));
    }

    public PostResponse unlikePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post of id=[%s] not found".formatted(postId), postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id=[%s] not found".formatted(userId), userId));

        post.removeLike(user);
        return postMapper.mapToResponse(postRepository.save(post));
    }

    public boolean hasUserLikedPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post of id=[%s] not found".formatted(postId), postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id=[%s] not found".formatted(userId), userId));

        return post.getLikedByUsers().contains(user);
    }
}
