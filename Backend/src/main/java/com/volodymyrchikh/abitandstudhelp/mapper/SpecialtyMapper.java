package com.volodymyrchikh.abitandstudhelp.mapper;

import com.volodymyrchikh.abitandstudhelp.domain.Specialty;
import com.volodymyrchikh.abitandstudhelp.dto.SpecialtyRequest;
import com.volodymyrchikh.abitandstudhelp.dto.SpecialtyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SpecialtyMapper {

    SpecialtyResponse mapToSpecialtyResponse(Specialty specialty);

    Specialty mapToSpecialty(SpecialtyRequest specialtyRequest);

    void updateSpecialtyFromRequest(SpecialtyRequest specialtyRequest, @MappingTarget Specialty specialtyToUpdate);
}
