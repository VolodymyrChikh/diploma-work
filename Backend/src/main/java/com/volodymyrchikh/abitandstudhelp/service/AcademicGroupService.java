package com.volodymyrchikh.abitandstudhelp.service;

import com.volodymyrchikh.abitandstudhelp.domain.AcademicGroup;
import com.volodymyrchikh.abitandstudhelp.dto.AcademicGroupRequest;
import com.volodymyrchikh.abitandstudhelp.dto.AcademicGroupResponse;
import com.volodymyrchikh.abitandstudhelp.exception.AcademicGroupNotFoundException;
import com.volodymyrchikh.abitandstudhelp.mapper.AcademicGroupMapper;
import com.volodymyrchikh.abitandstudhelp.repository.AcademicGroupRepository;
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
public class AcademicGroupService {

    private final AcademicGroupRepository repository;
    private final AcademicGroupMapper mapper;

    public List<AcademicGroupResponse> getAll() {
        return repository.findAllByOrderByIdAsc()
                .stream()
                .map(mapper::mapToResponse)
                .collect(Collectors.toList());
    }

    public Page<AcademicGroupResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::mapToResponse);
    }

    public AcademicGroupResponse getById(Long id) {
        AcademicGroup academicGroup = repository.findById(id)
                .orElseThrow(() -> new AcademicGroupNotFoundException("Академічна група не знайдена"));
        return mapper.mapToResponse(academicGroup);
    }

    @Transactional
    public AcademicGroupResponse create(AcademicGroupRequest request) {
        AcademicGroup academicGroup = AcademicGroup.builder()
                .name(request.getName())
                .build();
        return mapper.mapToResponse(repository.save(academicGroup));
    }

    @Transactional
    public AcademicGroupResponse update(Long id, AcademicGroupRequest request) {
        AcademicGroup academicGroup = repository.findById(id)
                .orElseThrow(() -> new AcademicGroupNotFoundException("Академічна група не знайдена"));
        academicGroup.setName(request.getName());
        return mapper.mapToResponse(repository.save(academicGroup));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new AcademicGroupNotFoundException("Академічна група не знайдена");
        }
        repository.deleteById(id);
    }
}

