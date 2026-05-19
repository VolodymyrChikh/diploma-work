package com.volodymyrchikh.abitandstudhelp.dto;

import com.volodymyrchikh.abitandstudhelp.common.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MediaResourceResponse {
    private UUID id;
    private String title;
    private String description;
    private String url;
    private List<String> fileUrls;
    private ResourceType type;
    private FileCategoryResponse category;
    private Integer views;
    private LocalDateTime createdAt;
}

