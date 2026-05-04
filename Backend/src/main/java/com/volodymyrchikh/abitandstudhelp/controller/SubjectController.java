package com.volodymyrchikh.abitandstudhelp.controller;

import com.querydsl.core.types.Predicate;
import com.volodymyrchikh.abitandstudhelp.domain.Subject;
import com.volodymyrchikh.abitandstudhelp.dto.SubjectRequest;
import com.volodymyrchikh.abitandstudhelp.dto.SubjectResponse;
import com.volodymyrchikh.abitandstudhelp.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    public SubjectResponse create(@RequestBody @Valid SubjectRequest request) {
        return subjectService.create(request);
    }

    @GetMapping("/{id}")
    public SubjectResponse getById(@PathVariable Long id) {
        return subjectService.getById(id);
    }

    @GetMapping
    public Page<SubjectResponse> getAll(@PageableDefault Pageable pageable,
                                        @QuerydslPredicate(root = Subject.class) Predicate filter) {
        return subjectService.getAll(pageable, filter);
    }

    @PutMapping("/{id}")
    public SubjectResponse update(@PathVariable Long id, @RequestBody @Valid SubjectRequest request) {
        return subjectService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        subjectService.delete(id);
    }
}

