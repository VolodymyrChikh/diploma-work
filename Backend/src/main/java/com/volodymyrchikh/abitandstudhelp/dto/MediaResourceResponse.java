package com.volodymyrchikh.abitandstudhelp.dto;

import com.volodymyrchikh.abitandstudhelp.common.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    private ResourceType type;
    private CategoryResponse category;
    private Integer views;
    private LocalDateTime createdAt;
}

