package com.volodymyrchikh.abitandstudhelp.mapper;

import com.volodymyrchikh.abitandstudhelp.domain.SelectiveGroup;
import com.volodymyrchikh.abitandstudhelp.domain.Specialty;
import com.volodymyrchikh.abitandstudhelp.dto.SelectiveGroupRequest;
import com.volodymyrchikh.abitandstudhelp.dto.SelectiveGroupResponse;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SelectiveGroupMapper {

    @Mapping(target = "specialtyIds", source = "specialties")
    SelectiveGroupResponse toResponse(SelectiveGroup selectiveGroup);

    List<SelectiveGroupResponse> toResponseList(List<SelectiveGroup> selectiveGroups);

    @Mapping(target = "specialties", source = "specialtyIds")
    SelectiveGroup toEntity(SelectiveGroupRequest request);

    @Mapping(target = "specialties", source = "specialtyIds")
    void updateEntityFromRequest(SelectiveGroupRequest request, @MappingTarget SelectiveGroup selectiveGroup);

    default Set<Long> mapSpecialtiesToIds(Set<Specialty> specialties) {
        if (specialties == null) {
            return null;
        }
        return specialties.stream()
                .map(Specialty::getId)
                .collect(Collectors.toSet());
    }

    default Set<Specialty> mapIdsToSpecialties(Set<Long> specialtyIds) {
        if (specialtyIds == null) {
            return null;
        }
        return specialtyIds.stream()
                .map(id -> {
                    Specialty specialty = new Specialty();
                    specialty.setId(id);
                    return specialty;
                })
                .collect(Collectors.toSet());
    }
}