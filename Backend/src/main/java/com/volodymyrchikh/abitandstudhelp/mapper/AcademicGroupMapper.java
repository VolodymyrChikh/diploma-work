package com.volodymyrchikh.abitandstudhelp.mapper;

import com.volodymyrchikh.abitandstudhelp.domain.AcademicGroup;
import com.volodymyrchikh.abitandstudhelp.dto.AcademicGroupResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AcademicGroupMapper {

	AcademicGroupResponse mapToResponse(AcademicGroup academicGroup);
}


