package com.volodymyrchikh.abitandstudhelp.controller;

import com.volodymyrchikh.abitandstudhelp.dto.AcademicGroupRequest;
import com.volodymyrchikh.abitandstudhelp.dto.AcademicGroupResponse;
import com.volodymyrchikh.abitandstudhelp.service.AcademicGroupService;
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
@RequestMapping("/api/academic-groups")
@RequiredArgsConstructor
public class AcademicGroupController {

    private final AcademicGroupService service;

    @GetMapping
    public List<AcademicGroupResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/paginated")
    public Page<AcademicGroupResponse> getAllPaginated(@PageableDefault Pageable pageable) {
        return service.getAll(pageable);
    }

    @GetMapping("/{id}")
    public AcademicGroupResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AcademicGroupResponse create(@RequestBody @Valid AcademicGroupRequest request) {
        return service.create(request);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public AcademicGroupResponse update(@PathVariable Long id, @RequestBody @Valid AcademicGroupRequest request) {
        return service.update(id, request);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

