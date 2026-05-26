package com.volodymyrchikh.abitandstudhelp.service;

import com.volodymyrchikh.abitandstudhelp.domain.SelectiveGroup;
import com.volodymyrchikh.abitandstudhelp.dto.SelectiveGroupRequest;
import com.volodymyrchikh.abitandstudhelp.dto.SelectiveGroupResponse;
import com.volodymyrchikh.abitandstudhelp.mapper.SelectiveGroupMapper;
import com.volodymyrchikh.abitandstudhelp.repository.SelectiveGroupRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SelectiveGroupService {

    private final SelectiveGroupRepository repository;
    private final SelectiveGroupMapper mapper;

    @Transactional
    public SelectiveGroupResponse create(SelectiveGroupRequest request) {
        SelectiveGroup selectiveGroup = mapper.toEntity(request);
        SelectiveGroup savedGroup = repository.save(selectiveGroup);
        return mapper.toResponse(savedGroup);
    }

    public SelectiveGroupResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Selective group not found with id: " + id));
    }

    public List<SelectiveGroupResponse> getAll() {
        return mapper.toResponseList(repository.findAll());
    }

    public List<SelectiveGroupResponse> getBySpecialtyAndSemester(Long specialtyId, Integer semester) {
        List<SelectiveGroup> groups = repository.findBySpecialtyIdAndSemester(specialtyId, semester);
        return mapper.toResponseList(groups);
    }

    @Transactional
    public SelectiveGroupResponse update(Long id, SelectiveGroupRequest request) {
        SelectiveGroup selectiveGroup = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Selective group not found with id: " + id));

        mapper.updateEntityFromRequest(request, selectiveGroup);
        return mapper.toResponse(selectiveGroup); // Hibernate збереже зміни автоматично завдяки @Transactional
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Selective group not found with id: " + id);
        }
        repository.deleteById(id);
    }
}