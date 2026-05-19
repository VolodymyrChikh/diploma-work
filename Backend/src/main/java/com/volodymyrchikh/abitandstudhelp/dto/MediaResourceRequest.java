package com.volodymyrchikh.abitandstudhelp.dto;

import com.volodymyrchikh.abitandstudhelp.common.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MediaResourceRequest {

    @NotBlank(message = "Назва матеріалу не може бути пустою")
    private String title;

    private String description;

    private String url;

    private List<String> fileUrls;

    @NotNull(message = "Тип матеріалу не може бути пустим")
    private ResourceType type;

    @NotNull(message = "Категорія матеріалу не може бути пустою")
    private Long categoryId;
}
