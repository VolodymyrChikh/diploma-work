package com.volodymyrchikh.abitandstudhelp.controller;

import com.querydsl.core.types.Predicate;
import com.volodymyrchikh.abitandstudhelp.domain.Specialty;
import com.volodymyrchikh.abitandstudhelp.dto.SpecialtyRequest;
import com.volodymyrchikh.abitandstudhelp.dto.SpecialtyResponse;
import com.volodymyrchikh.abitandstudhelp.service.SpecialtyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/specialties")
@RestController
@RequiredArgsConstructor
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @PostMapping
    public Specialty create(@RequestBody @Valid SpecialtyRequest specialtyRequest) {
        return specialtyService.create(specialtyRequest);
    }

    @GetMapping
    public Page<SpecialtyResponse> getAll(@PageableDefault Pageable pageable,
                                          @QuerydslPredicate(root = Specialty.class) Predicate filter) {
        return specialtyService.getAll(pageable, filter);
    }

    @GetMapping("/{id}")
    public SpecialtyResponse getById(@PathVariable Long id) {
        return specialtyService.getById(id);
    }

    @GetMapping("/by-name/{name}")
    public SpecialtyResponse getByName(@PathVariable String name) {
        return specialtyService.getByName(name);
    }

    @PutMapping("/{id}")
    public SpecialtyResponse update(@PathVariable Long id, @RequestBody SpecialtyRequest specialtyRequest) {
        return specialtyService.update(id, specialtyRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        specialtyService.delete(id);
    }

}
