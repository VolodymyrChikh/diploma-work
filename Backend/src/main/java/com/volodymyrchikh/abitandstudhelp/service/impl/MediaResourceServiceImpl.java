package com.volodymyrchikh.abitandstudhelp.service.impl;

import com.volodymyrchikh.abitandstudhelp.common.ResourceType;
import com.volodymyrchikh.abitandstudhelp.domain.FileCategory;
import com.volodymyrchikh.abitandstudhelp.domain.MediaResource;
import com.volodymyrchikh.abitandstudhelp.dto.FileCategoryResponse;
import com.volodymyrchikh.abitandstudhelp.dto.MediaResourceRequest;
import com.volodymyrchikh.abitandstudhelp.dto.MediaResourceResponse;
import com.volodymyrchikh.abitandstudhelp.mapper.FileCategoryMapper;
import com.volodymyrchikh.abitandstudhelp.mapper.MediaResourceMapper;
import com.volodymyrchikh.abitandstudhelp.repository.FileCategoryRepository;
import com.volodymyrchikh.abitandstudhelp.repository.MediaResourceRepository;
import com.volodymyrchikh.abitandstudhelp.service.MediaResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MediaResourceServiceImpl implements MediaResourceService {

    private final MediaResourceRepository repository;
    private final FileCategoryRepository categoryRepository;
    private final MediaResourceMapper mapper;
    private final FileCategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<MediaResourceResponse> getAll(String categoryName, ResourceType type, Pageable pageable) {
        if (categoryName != null && type != null) {
            return repository.findAllByCategoryNameAndType(categoryName, type, pageable).map(mapper::toResponse);
        } else if (categoryName != null) {
            return repository.findAllByCategoryName(categoryName, pageable).map(mapper::toResponse);
        } else if (type != null) {
            return repository.findAllByType(type, pageable).map(mapper::toResponse);
        }
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public MediaResourceResponse getById(UUID id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Матеріал не знайдено"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MediaResourceResponse> search(String query, Pageable pageable) {
        return repository.search(query, pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaResourceResponse> getLatest() {
        return repository.findTop5ByOrderByCreatedAtDesc(PageRequest.of(0, 5))
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MediaResourceResponse create(MediaResourceRequest request) {
        FileCategory category = findMediaCategory(request.getCategoryId());

        MediaResource entity = mapper.toEntity(request);
        applyFileUrls(entity, request);
        entity.setCategory(category);
        entity.setViews(0);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public MediaResourceResponse update(UUID id, MediaResourceRequest request) {
        MediaResource entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Матеріал не знайдено"));

        FileCategory category = findMediaCategory(request.getCategoryId());

        mapper.updateEntity(entity, request);
        applyFileUrls(entity, request);
        entity.setCategory(category);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Матеріал не знайдено");
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileCategoryResponse> getCategories() {
        return categoryRepository.findAllByOrderByIdAsc()
                .stream()
                .map(categoryMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void incrementViews(UUID id) {
        MediaResource entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Матеріал не знайдено"));
        if (entity.getViews() == null) {
            entity.setViews(0);
        }
        entity.setViews(entity.getViews() + 1);
        repository.save(entity);
    }

    private void applyFileUrls(MediaResource entity, MediaResourceRequest request) {
        List<String> fileUrls = normalizeFileUrls(request);
        entity.setFileUrls(new java.util.ArrayList<>(fileUrls));
        entity.setUrl(fileUrls.get(0));
    }

    private List<String> normalizeFileUrls(MediaResourceRequest request) {
        List<String> fileUrls = request.getFileUrls() == null ? List.of() : request.getFileUrls().stream()
                .filter(url -> url != null && !url.isBlank())
                .map(String::trim)
                .toList();

        if (!fileUrls.isEmpty()) {
            validateUrls(request.getType(), fileUrls);
            return List.copyOf(fileUrls);
        }

        if (request.getUrl() != null && !request.getUrl().isBlank()) {
            String url = request.getUrl().trim();
            validateUrls(request.getType(), List.of(url));
            return List.of(url);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Додайте хоча б одне посилання на файл");
    }

    private FileCategory findMediaCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Категорію медіатеки не знайдено"));
    }

    private void validateUrls(ResourceType type, List<String> urls) {
        for (String url : urls) {
            if (type == ResourceType.EXTERNAL_LINK && !isHttpUrl(url)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Для зовнішнього посилання потрібен коректний http або https URL"
                );
            }

            if (!isHttpUrl(url) && !isSameOriginFileUrl(url)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Посилання на файл має бути коректним URL");
            }
        }
    }

    private boolean isHttpUrl(String value) {
        try {
            URI uri = new URI(value);
            return ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                    && uri.getHost() != null;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private boolean isSameOriginFileUrl(String value) {
        try {
            URI uri = new URI(value);
            return uri.getScheme() == null && value.startsWith("/api/media/files/");
        } catch (Exception e) {
            return false;
        }
    }
}
