package com.volodymyrchikh.abitandstudhelp.mapper;

import com.volodymyrchikh.abitandstudhelp.domain.Category;
import com.volodymyrchikh.abitandstudhelp.dto.CategoryRequest;
import com.volodymyrchikh.abitandstudhelp.dto.CategoryResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    Category mapToCategory(CategoryRequest request);

    CategoryResponse mapToResponse(Category category);

    void updateCategoryFromRequest(CategoryRequest request, @MappingTarget Category categoryToUpdate);
}
