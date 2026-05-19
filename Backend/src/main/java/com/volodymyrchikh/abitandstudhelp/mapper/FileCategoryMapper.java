package com.volodymyrchikh.abitandstudhelp.mapper;

import com.volodymyrchikh.abitandstudhelp.domain.FileCategory;
import com.volodymyrchikh.abitandstudhelp.dto.FileCategoryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileCategoryMapper {

    FileCategoryResponse mapToResponse(FileCategory fileCategory);
}

