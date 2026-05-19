package com.volodymyrchikh.abitandstudhelp.service;

import com.volodymyrchikh.abitandstudhelp.common.ResourceType;
import com.volodymyrchikh.abitandstudhelp.dto.FileCategoryResponse;
import com.volodymyrchikh.abitandstudhelp.dto.MediaResourceRequest;
import com.volodymyrchikh.abitandstudhelp.dto.MediaResourceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MediaResourceService {
    Page<MediaResourceResponse> getAll(String categoryName, ResourceType type, Pageable pageable);

    MediaResourceResponse getById(UUID id);

    Page<MediaResourceResponse> search(String query, Pageable pageable);

    List<MediaResourceResponse> getLatest();

    MediaResourceResponse create(MediaResourceRequest request);

    MediaResourceResponse update(UUID id, MediaResourceRequest request);

    void delete(UUID id);

    List<FileCategoryResponse> getCategories();

    void incrementViews(UUID id);
}

