package com.volodymyrchikh.abitandstudhelp.mapper;

import com.volodymyrchikh.abitandstudhelp.common.CategoryType;
import com.volodymyrchikh.abitandstudhelp.domain.Category;
import com.volodymyrchikh.abitandstudhelp.domain.Post;
import com.volodymyrchikh.abitandstudhelp.domain.User;
import com.volodymyrchikh.abitandstudhelp.dto.PostRequest;
import com.volodymyrchikh.abitandstudhelp.dto.PostResponse;
import com.volodymyrchikh.abitandstudhelp.exception.CategoryNotFoundException;
import com.volodymyrchikh.abitandstudhelp.exception.UserNotFoundException;
import com.volodymyrchikh.abitandstudhelp.repository.CategoryRepository;
import com.volodymyrchikh.abitandstudhelp.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public PostMapper(UserMapper userMapper,
                      CategoryMapper categoryMapper,
                      CategoryRepository categoryRepository,
                      UserRepository userRepository
    ) {
        this.userMapper = userMapper;
        this.categoryMapper = categoryMapper;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public PostResponse mapToResponse(Post post){
        return PostResponse.builder()
                .id(post.getId())
                .slug(post.getSlug())
                .title(post.getTitle())
                .likes(post.getLikes())
                .isAnonymous(post.getIsAnonymous())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .categoryResponse(categoryMapper.mapToResponse(post.getCategory()))
                .userResponse(userMapper.mapToResponse(post.getUser()))
                .build();

    }

    public void updatePostFromRequest(PostRequest postRequest, Post postToUpdate) {
        if (postRequest == null || postToUpdate == null) {
            return;
        }

        if (postRequest.getTitle() != null) {
            postToUpdate.setTitle(postRequest.getTitle());
        }
        if (postRequest.getLikes() != null) {
            postToUpdate.setLikes(postRequest.getLikes());
        }

        if (postRequest.getCategoryId() != null) {
            Category category = categoryRepository.findByIdAndType(postRequest.getCategoryId(), CategoryType.FORUM)
                    .orElseThrow(() -> new CategoryNotFoundException("Категорію форуму не знайдено",
                            postRequest.getCategoryId()));
            postToUpdate.setCategory(category);
        }

        if (postRequest.getUserId() != null) {
            User user = userRepository.findById(postRequest.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("Користувача не знайдено", postRequest.getUserId()));
            postToUpdate.setUser(user);
        }
    }
}
