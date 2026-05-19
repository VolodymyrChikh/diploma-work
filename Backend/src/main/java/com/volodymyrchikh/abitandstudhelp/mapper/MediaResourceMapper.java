package com.volodymyrchikh.abitandstudhelp.mapper;

import com.volodymyrchikh.abitandstudhelp.domain.MediaResource;
import com.volodymyrchikh.abitandstudhelp.dto.MediaResourceRequest;
import com.volodymyrchikh.abitandstudhelp.dto.MediaResourceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {FileCategoryMapper.class})
public interface MediaResourceMapper {
    MediaResourceResponse toResponse(MediaResource mediaResource);

    MediaResource toEntity(MediaResourceRequest request);

    void updateEntity(@MappingTarget MediaResource mediaResource, MediaResourceRequest request);
}

