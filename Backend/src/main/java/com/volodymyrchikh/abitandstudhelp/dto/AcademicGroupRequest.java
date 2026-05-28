package com.volodymyrchikh.abitandstudhelp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AcademicGroupRequest {

    @NotBlank(message = "Назва групи не може бути пустою")
    private String name;
}

