package com.volodymyrchikh.abitandstudhelp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileCategoryResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

