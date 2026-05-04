package com.volodymyrchikh.abitandstudhelp.service;

import com.querydsl.core.types.Predicate;
import com.volodymyrchikh.abitandstudhelp.common.DegreeLevel;
import com.volodymyrchikh.abitandstudhelp.domain.Specialty;
import com.volodymyrchikh.abitandstudhelp.domain.Subject;
import com.volodymyrchikh.abitandstudhelp.dto.SubjectRequest;
import com.volodymyrchikh.abitandstudhelp.dto.SubjectResponse;
import com.volodymyrchikh.abitandstudhelp.exception.SpecialtyNotFoundException;
import com.volodymyrchikh.abitandstudhelp.mapper.SubjectMapper;
import com.volodymyrchikh.abitandstudhelp.repository.SpecialtyRepository;
import com.volodymyrchikh.abitandstudhelp.repository.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final SpecialtyRepository specialtyRepository;
    private final SubjectMapper subjectMapper;

    public SubjectResponse create(SubjectRequest request) {
        Subject subject = subjectMapper.mapToSubject(request);
        if (subject.getDegreeLevel() == null) {
            subject.setDegreeLevel(DegreeLevel.BACHELOR);
        }
        subject.setSpecialty(getSpecialtyById(request.getSpecialtyId()));
        return subjectMapper.mapToSubjectResponse(subjectRepository.save(subject));
    }

    public SubjectResponse getById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id " + id));
        return subjectMapper.mapToSubjectResponse(subject);
    }

    public Page<SubjectResponse> getAll(Pageable pageable, Predicate filter) {
        return subjectRepository.findAll(filter, pageable)
                .map(subjectMapper::mapToSubjectResponse);
    }

    public SubjectResponse update(Long id, SubjectRequest request) {
        Subject existing = subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id " + id));
        subjectMapper.updateSubjectFromRequest(request, existing);
        if (existing.getDegreeLevel() == null) {
            existing.setDegreeLevel(DegreeLevel.BACHELOR);
        }
        existing.setSpecialty(getSpecialtyById(request.getSpecialtyId()));
        return subjectMapper.mapToSubjectResponse(subjectRepository.save(existing));
    }

    public void delete(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new EntityNotFoundException("Subject not found with id " + id);
        }
        subjectRepository.deleteById(id);
    }

    private Specialty getSpecialtyById(Long specialtyId) {
        return specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new SpecialtyNotFoundException(
                        "Specialty of id=[%s] or name=[%s] not found".formatted(specialtyId, null),
                        specialtyId,
                        null
                ));
    }
}

