package com.volodymyrchikh.abitandstudhelp.service;

import com.volodymyrchikh.abitandstudhelp.common.CategoryType;
import com.volodymyrchikh.abitandstudhelp.domain.Category;
import com.volodymyrchikh.abitandstudhelp.dto.CategoryResponse;
import com.volodymyrchikh.abitandstudhelp.mapper.CategoryMapper;
import com.volodymyrchikh.abitandstudhelp.repository.CategoryRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CategoryServiceTest {

    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final CategoryMapper categoryMapper = mock(CategoryMapper.class);
    private final CategoryService categoryService = new CategoryService(categoryRepository, categoryMapper);

    @Test
    void getByTypeReturnsOnlyCategoriesForRequestedType() {
        Category forumCategory = Category.builder()
                .id(11L)
                .name("Гумор")
                .type(CategoryType.FORUM)
                .build();
        CategoryResponse forumResponse = new CategoryResponse();
        forumResponse.setId(11L);
        forumResponse.setName("Гумор");
        forumResponse.setType(CategoryType.FORUM);

        when(categoryRepository.findAllByTypeOrderByIdAsc(CategoryType.FORUM))
                .thenReturn(List.of(forumCategory));
        when(categoryMapper.mapToResponse(forumCategory)).thenReturn(forumResponse);

        List<CategoryResponse> result = categoryService.getByType(CategoryType.FORUM);

        assertEquals(List.of(forumResponse), result);
        verify(categoryRepository).findAllByTypeOrderByIdAsc(CategoryType.FORUM);
    }
}
