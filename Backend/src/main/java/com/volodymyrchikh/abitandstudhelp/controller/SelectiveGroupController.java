package com.volodymyrchikh.abitandstudhelp.controller;

import com.volodymyrchikh.abitandstudhelp.dto.SelectiveGroupRequest;
import com.volodymyrchikh.abitandstudhelp.dto.SelectiveGroupResponse;
import com.volodymyrchikh.abitandstudhelp.service.SelectiveGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/selective-groups")
@RequiredArgsConstructor
public class SelectiveGroupController {

    private final SelectiveGroupService service;

    @PostMapping
    public ResponseEntity<SelectiveGroupResponse> create(@Valid @RequestBody SelectiveGroupRequest request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SelectiveGroupResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<SelectiveGroupResponse>> getAll(
            @RequestParam(required = false) Long specialtyId,
            @RequestParam(required = false) Integer semester
    ) {
        if (specialtyId != null && semester != null) {
            return ResponseEntity.ok(service.getBySpecialtyAndSemester(specialtyId, semester));
        }
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SelectiveGroupResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SelectiveGroupRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}