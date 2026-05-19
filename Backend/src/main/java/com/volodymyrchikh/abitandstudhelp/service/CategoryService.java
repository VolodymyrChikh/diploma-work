package com.volodymyrchikh.abitandstudhelp.service;

import com.querydsl.core.types.Predicate;
import com.volodymyrchikh.abitandstudhelp.common.CategoryType;
import com.volodymyrchikh.abitandstudhelp.domain.Category;
import com.volodymyrchikh.abitandstudhelp.dto.CategoryRequest;
import com.volodymyrchikh.abitandstudhelp.dto.CategoryResponse;
import com.volodymyrchikh.abitandstudhelp.mapper.CategoryMapper;
import com.volodymyrchikh.abitandstudhelp.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryResponse create(CategoryRequest request) {
        Category category = categoryMapper.mapToCategory(request);
        if (category.getType() == null) {
            category.setType(CategoryType.GENERAL);
        }
        return categoryMapper.mapToResponse(categoryRepository.save(category));
    }

    public CategoryResponse getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + id));
        return categoryMapper.mapToResponse(category);
    }

    public Page<CategoryResponse> getAll(Pageable pageable, Predicate filter) {
        return categoryRepository.findAll(filter, pageable)
                .map(categoryMapper::mapToResponse);
    }

    public List<CategoryResponse> getByType(CategoryType type) {
        return categoryRepository.findAllByTypeOrderByIdAsc(type).stream()
                .map(categoryMapper::mapToResponse)
                .toList();
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + id));
        categoryMapper.updateCategoryFromRequest(request, existing);
        return categoryMapper.mapToResponse(categoryRepository.save(existing));
    }

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found with id " + id);
        }
        categoryRepository.deleteById(id);
    }
}
