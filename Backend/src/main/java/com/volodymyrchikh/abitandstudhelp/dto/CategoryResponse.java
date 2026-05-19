package com.volodymyrchikh.abitandstudhelp.dto;

import com.volodymyrchikh.abitandstudhelp.common.CategoryType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryResponse {

    private Long id;
    private String name;
    private CategoryType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
