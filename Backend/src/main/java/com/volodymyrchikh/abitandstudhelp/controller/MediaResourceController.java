package com.volodymyrchikh.abitandstudhelp.controller;

import com.volodymyrchikh.abitandstudhelp.common.ResourceType;
import com.volodymyrchikh.abitandstudhelp.dto.CategoryResponse;
import com.volodymyrchikh.abitandstudhelp.dto.MediaResourceRequest;
import com.volodymyrchikh.abitandstudhelp.dto.MediaResourceResponse;
import com.volodymyrchikh.abitandstudhelp.dto.StorageUploadResponse;
import com.volodymyrchikh.abitandstudhelp.service.MediaResourceService;
import com.volodymyrchikh.abitandstudhelp.service.storage.SupabaseStorageService;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaResourceController {

    private final MediaResourceService service;
    private final SupabaseStorageService storageService;

    @GetMapping
    public Page<MediaResourceResponse> getAll(
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) ResourceType type,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.getAll(categoryName, type, pageable);
    }

    @GetMapping("/{id}")
    public MediaResourceResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping("/search")
    public Page<MediaResourceResponse> search(
            @RequestParam String query,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.search(query, pageable);
    }

    @GetMapping("/latest")
    public List<MediaResourceResponse> getLatest() {
        return service.getLatest();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public MediaResourceResponse create(@RequestBody @Valid MediaResourceRequest request) {
        return service.create(request);
    }

    /**
     * Create media resource via multipart file upload. The files will be uploaded to Supabase storage
     * and the resulting public URLs will be saved in the resource record.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MediaResourceResponse createWithFile(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("type") ResourceType type,
            @RequestParam("categoryId") Long categoryId
    ) throws Exception {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("At least one file is required");
        }

        List<StorageUploadResponse> uploads = new java.util.ArrayList<>();
        for (MultipartFile file : files) {
            uploads.add(storageService.uploadFile(file));
        }

        List<String> fileUrls = uploads.stream()
                .map(StorageUploadResponse::getUrl)
                .toList();

        MediaResourceRequest req = MediaResourceRequest.builder()
                .title(title)
                .description(description)
                .url(fileUrls.get(0))
                .fileUrls(fileUrls)
                .type(type)
                .categoryId(categoryId)
                .build();

        return service.create(req);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public MediaResourceResponse update(@PathVariable UUID id, @RequestBody @Valid MediaResourceRequest request) {
        return service.update(id, request);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @GetMapping("/categories")
    public List<CategoryResponse> getCategories() {
        return service.getCategories();
    }

    @PostMapping("/{id}/increment-views")
    public void incrementViews(@PathVariable UUID id) {
        service.incrementViews(id);
    }
}

