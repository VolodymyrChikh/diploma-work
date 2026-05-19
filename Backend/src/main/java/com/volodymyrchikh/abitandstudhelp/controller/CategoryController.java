package com.volodymyrchikh.abitandstudhelp.controller;

import com.querydsl.core.types.Predicate;
import com.volodymyrchikh.abitandstudhelp.common.CategoryType;
import com.volodymyrchikh.abitandstudhelp.domain.Category;
import com.volodymyrchikh.abitandstudhelp.dto.CategoryRequest;
import com.volodymyrchikh.abitandstudhelp.dto.CategoryResponse;
import com.volodymyrchikh.abitandstudhelp.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryResponse create(@RequestBody @Valid CategoryRequest request) {
        return categoryService.create(request);
    }

    @GetMapping("/{id}")
    public CategoryResponse getById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @GetMapping
    public Page<CategoryResponse> getAll(@PageableDefault Pageable pageable,
                                         @QuerydslPredicate(root = Category.class) Predicate filter) {
        return categoryService.getAll(pageable, filter);
    }

    @GetMapping("/forum")
    public List<CategoryResponse> getForumCategories() {
        return categoryService.getByType(CategoryType.FORUM);
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id, @RequestBody @Valid CategoryRequest request) {
        return categoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}
