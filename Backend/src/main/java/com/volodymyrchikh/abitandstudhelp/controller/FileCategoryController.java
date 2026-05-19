package com.volodymyrchikh.abitandstudhelp.controller;

import com.volodymyrchikh.abitandstudhelp.dto.FileCategoryRequest;
import com.volodymyrchikh.abitandstudhelp.dto.FileCategoryResponse;
import com.volodymyrchikh.abitandstudhelp.service.FileCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/file-categories")
@RequiredArgsConstructor
public class FileCategoryController {

    private final FileCategoryService service;

    @GetMapping
    public List<FileCategoryResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/paginated")
    public Page<FileCategoryResponse> getAllPaginated(@PageableDefault Pageable pageable) {
        return service.getAll(pageable);
    }

    @GetMapping("/{id}")
    public FileCategoryResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FileCategoryResponse create(@RequestBody @Valid FileCategoryRequest request) {
        return service.create(request);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public FileCategoryResponse update(@PathVariable Long id, @RequestBody @Valid FileCategoryRequest request) {
        return service.update(id, request);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

