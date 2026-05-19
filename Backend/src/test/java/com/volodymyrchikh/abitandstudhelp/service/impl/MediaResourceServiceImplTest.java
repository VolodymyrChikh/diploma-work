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
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MediaResourceServiceImplTest {

    private final MediaResourceRepository mediaResourceRepository = mock(MediaResourceRepository.class);
    private final FileCategoryRepository categoryRepository = mock(FileCategoryRepository.class);
    private final MediaResourceMapper mediaResourceMapper = mock(MediaResourceMapper.class);
    private final FileCategoryMapper categoryMapper = mock(FileCategoryMapper.class);
    private final MediaResourceServiceImpl service = new MediaResourceServiceImpl(
            mediaResourceRepository,
            categoryRepository,
            mediaResourceMapper,
            categoryMapper
    );

    @Test
    void getCategoriesReturnsOnlyMediaCategories() {
        FileCategory mediaCategory = FileCategory.builder()
                .id(5L)
                .name("Документи")
                .description("Матеріали та файли")
                .build();
        FileCategoryResponse response = new FileCategoryResponse();
        response.setId(5L);
        response.setName("Документи");
        response.setDescription("Матеріали та файли");

        when(categoryRepository.findAllByOrderByIdAsc())
                .thenReturn(List.of(mediaCategory));
        when(categoryMapper.mapToResponse(mediaCategory)).thenReturn(response);

        List<FileCategoryResponse> result = service.getCategories();

        assertEquals(List.of(response), result);
        verify(categoryRepository).findAllByOrderByIdAsc();
    }

    @Test
    void createRejectsExternalLinksWithoutHttpUrl() {
        FileCategory mediaCategory = FileCategory.builder()
                .id(5L)
                .name("Лінки")
                .description("Лінки")
                .build();

        when(categoryRepository.findById(5L))
                .thenReturn(Optional.of(mediaCategory));

        MediaResourceRequest request = MediaResourceRequest.builder()
                .title("Корисний ресурс")
                .type(ResourceType.EXTERNAL_LINK)
                .categoryId(5L)
                .url("ftp://example.com/file")
                .build();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.create(request));

        assertEquals("Для зовнішнього посилання потрібен коректний http або https URL", exception.getReason());
    }

    @Test
    void createRejectsMissingMediaUrlsWithUkrainianMessage() {
        FileCategory mediaCategory = FileCategory.builder()
                .id(5L)
                .name("Документи")
                .description("Документи")
                .build();

        when(categoryRepository.findById(5L))
                .thenReturn(Optional.of(mediaCategory));

        MediaResourceRequest request = MediaResourceRequest.builder()
                .title("Документ")
                .type(ResourceType.DOCUMENT)
                .categoryId(5L)
                .fileUrls(List.of(" "))
                .build();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.create(request));

        assertEquals("Додайте хоча б одне посилання на файл", exception.getReason());
    }

    @Test
    void createStoresTrimmedHttpUrls() {
        FileCategory mediaCategory = FileCategory.builder()
                .id(5L)
                .name("Документи")
                .description("Документи")
                .build();
        MediaResource entity = new MediaResource();
        MediaResource saved = new MediaResource();
        MediaResourceResponse response = MediaResourceResponse.builder()
                .id(UUID.randomUUID())
                .url("https://example.com/file.pdf")
                .fileUrls(List.of("https://example.com/file.pdf"))
                .build();

        when(categoryRepository.findById(5L))
                .thenReturn(Optional.of(mediaCategory));
        when(mediaResourceMapper.toEntity(org.mockito.ArgumentMatchers.any(MediaResourceRequest.class))).thenReturn(entity);
        when(mediaResourceRepository.save(entity)).thenReturn(saved);
        when(mediaResourceMapper.toResponse(saved)).thenReturn(response);

        MediaResourceRequest request = MediaResourceRequest.builder()
                .title("Документ")
                .type(ResourceType.DOCUMENT)
                .categoryId(5L)
                .fileUrls(List.of(" https://example.com/file.pdf "))
                .build();

        MediaResourceResponse result = service.create(request);

        assertEquals(List.of("https://example.com/file.pdf"), entity.getFileUrls());
        assertEquals("https://example.com/file.pdf", entity.getUrl());
        assertEquals(response, result);
    }
}
