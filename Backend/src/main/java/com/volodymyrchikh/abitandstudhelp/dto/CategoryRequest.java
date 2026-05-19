package com.volodymyrchikh.abitandstudhelp.dto;

import com.volodymyrchikh.abitandstudhelp.common.CategoryType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank
    private String name;

    private CategoryType type;

}
