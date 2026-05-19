package com.volodymyrchikh.abitandstudhelp.service;

import com.volodymyrchikh.abitandstudhelp.domain.FileCategory;
import com.volodymyrchikh.abitandstudhelp.dto.FileCategoryRequest;
import com.volodymyrchikh.abitandstudhelp.dto.FileCategoryResponse;
import com.volodymyrchikh.abitandstudhelp.exception.FileCategoryNotFoundException;
import com.volodymyrchikh.abitandstudhelp.mapper.FileCategoryMapper;
import com.volodymyrchikh.abitandstudhelp.repository.FileCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileCategoryService {

    private final FileCategoryRepository repository;
    private final FileCategoryMapper mapper;

    public List<FileCategoryResponse> getAll() {
        return repository.findAllByOrderByIdAsc()
                .stream()
                .map(mapper::mapToResponse)
                .collect(Collectors.toList());
    }

    public Page<FileCategoryResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::mapToResponse);
    }

    public FileCategoryResponse getById(Long id) {
        FileCategory fileCategory = repository.findById(id)
                .orElseThrow(() -> new FileCategoryNotFoundException("Категорія файлів не знайдена"));
        return mapper.mapToResponse(fileCategory);
    }

    @Transactional
    public FileCategoryResponse create(FileCategoryRequest request) {
        FileCategory fileCategory = FileCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return mapper.mapToResponse(repository.save(fileCategory));
    }

    @Transactional
    public FileCategoryResponse update(Long id, FileCategoryRequest request) {
        FileCategory fileCategory = repository.findById(id)
                .orElseThrow(() -> new FileCategoryNotFoundException("Категорія файлів не знайдена"));
        fileCategory.setName(request.getName());
        fileCategory.setDescription(request.getDescription());
        return mapper.mapToResponse(repository.save(fileCategory));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new FileCategoryNotFoundException("Категорія файлів не знайдена");
        }
        repository.deleteById(id);
    }
}

