package com.volodymyrchikh.abitandstudhelp.service;

import com.volodymyrchikh.abitandstudhelp.common.CategoryType;
import com.volodymyrchikh.abitandstudhelp.domain.Category;
import com.volodymyrchikh.abitandstudhelp.domain.Post;
import com.volodymyrchikh.abitandstudhelp.domain.User;
import com.volodymyrchikh.abitandstudhelp.dto.PostRequest;
import com.volodymyrchikh.abitandstudhelp.dto.PostResponse;
import com.volodymyrchikh.abitandstudhelp.exception.CategoryNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.PostNotFoundException;
import com.volodymyrchikh.abitandstudhelp.mapper.PostMapper;
import com.volodymyrchikh.abitandstudhelp.repository.CategoryRepository;
import com.volodymyrchikh.abitandstudhelp.repository.PostRepository;
import com.volodymyrchikh.abitandstudhelp.repository.UserRepository;
import com.volodymyrchikh.abitandstudhelp.security.AuthorizationService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostServiceTest {

    private final PostRepository postRepository = mock(PostRepository.class);
    private final PostMapper postMapper = mock(PostMapper.class);
    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final NotificationService notificationService = mock(NotificationService.class);
    private final AuthorizationService authorizationService = mock(AuthorizationService.class);
    private final PostService postService = new PostService(
            postRepository,
            postMapper,
            categoryRepository,
            userRepository,
            notificationService,
            authorizationService
    );

    @Test
    void createUsesOnlyForumCategories() {
        PostRequest request = new PostRequest();
        request.setTitle("Смішний пост");
        request.setCategoryId(11L);
        request.setUserId(7L);
        request.setIsAnonymous(false);

        Category category = Category.builder()
                .id(11L)
                .name("Гумор")
                .type(CategoryType.FORUM)
                .build();
        User user = User.builder().id(7L).build();
        Post savedPost = Post.builder()
                .id(22L)
                .title("Смішний пост")
                .category(category)
                .user(user)
                .build();
        PostResponse response = PostResponse.builder().id(22L).build();

        when(categoryRepository.findByIdAndType(11L, CategoryType.FORUM))
                .thenReturn(Optional.of(category));
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(postRepository.existsBySlug("smisnii-post")).thenReturn(false);
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);
        when(postMapper.mapToResponse(savedPost)).thenReturn(response);

        PostResponse result = postService.create(request);

        assertEquals(response, result);
        verify(categoryRepository).findByIdAndType(11L, CategoryType.FORUM);
    }

    @Test
    void createUsesUkrainianMessageWhenForumCategoryMissing() {
        PostRequest request = new PostRequest();
        request.setTitle("Питання без категорії");
        request.setCategoryId(404L);
        request.setUserId(7L);

        when(categoryRepository.findByIdAndType(404L, CategoryType.FORUM))
                .thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(
                CategoryNotFoundException.class,
                () -> postService.create(request)
        );

        assertEquals("Категорію форуму не знайдено", exception.getMessage());
        assertEquals(404L, exception.getCategoryId());
    }

    @Test
    void getByIdUsesUkrainianMessageWhenPostMissing() {
        when(postRepository.findById(404L)).thenReturn(Optional.empty());

        PostNotFoundException exception = assertThrows(
                PostNotFoundException.class,
                () -> postService.getById(404L)
        );

        assertEquals("Допис не знайдено", exception.getMessage());
        assertEquals(404L, exception.getPostId());
    }
}
