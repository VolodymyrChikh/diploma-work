package com.volodymyrchikh.abitandstudhelp.service;

import com.querydsl.core.types.Predicate;
import com.volodymyrchikh.abitandstudhelp.domain.Specialty;
import com.volodymyrchikh.abitandstudhelp.dto.SpecialtyRequest;
import com.volodymyrchikh.abitandstudhelp.dto.SpecialtyResponse;
import com.volodymyrchikh.abitandstudhelp.exception.SpecialtyNotFoundException;
import com.volodymyrchikh.abitandstudhelp.mapper.SpecialtyMapper;
import com.volodymyrchikh.abitandstudhelp.repository.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    public Specialty create(SpecialtyRequest specialtyRequest) {
        Specialty specialty = specialtyMapper.mapToSpecialty(specialtyRequest);
        return specialtyRepository.save(specialty);
    }

    public SpecialtyResponse getById(Long id) {
        return specialtyMapper.mapToSpecialtyResponse(specialtyRepository.findById(id)
                .orElseThrow(() -> new SpecialtyNotFoundException("Specialty of id=[%s] or name=[%s] not found"
                        .formatted(id, null), id, null)));
    }

    public SpecialtyResponse getByName(String name) {
        return specialtyMapper.mapToSpecialtyResponse(specialtyRepository.findByName(name)
                    .orElseThrow(() -> new SpecialtyNotFoundException("Specialty of id=[%s] or name=[%s] not found"
                        .formatted(null, name), null, name)));
    }

    public Page<SpecialtyResponse> getAll(Pageable pageable, Predicate filter) {
        return specialtyRepository.findAll(filter, pageable)
                .map(specialtyMapper::mapToSpecialtyResponse);
    }

    public SpecialtyResponse update(Long id, SpecialtyRequest specialtyRequest) {
        Specialty specialtyToUpdate = specialtyRepository.findById(id)
                .orElseThrow(() -> new SpecialtyNotFoundException("Specialty of id=[%s] or name=[%s] not found"
                        .formatted(id, specialtyRequest.getName()), id, specialtyRequest.getName()));
        specialtyMapper.updateSpecialtyFromRequest(specialtyRequest, specialtyToUpdate);
        return specialtyMapper.mapToSpecialtyResponse(specialtyRepository.save(specialtyToUpdate));
    }

    public void delete(Long id) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new SpecialtyNotFoundException("Specialty of id=[%s] or name=[%s] not found"
                        .formatted(id, null), id, null));
        specialtyRepository.delete(specialty);
    }
}
