package com.volodymyrchikh.abitandstudhelp.mapper;

import com.volodymyrchikh.abitandstudhelp.domain.Subject;
import com.volodymyrchikh.abitandstudhelp.dto.SubjectRequest;
import com.volodymyrchikh.abitandstudhelp.dto.SubjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubjectMapper {

    @Mapping(target = "specialty", ignore = true)
    Subject mapToSubject(SubjectRequest request);

    @Mapping(target = "specialtyId", source = "specialty.id")
    @Mapping(target = "specialtyName", source = "specialty.name")
    @Mapping(target = "selectiveGroupId", source = "selectiveGroup.id")
    SubjectResponse mapToSubjectResponse(Subject subject);

    @Mapping(target = "specialty", ignore = true)
    void updateSubjectFromRequest(SubjectRequest request, @MappingTarget Subject subjectToUpdate);
}

