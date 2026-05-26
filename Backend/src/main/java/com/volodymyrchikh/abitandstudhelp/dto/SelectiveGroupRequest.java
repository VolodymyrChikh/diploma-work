package com.volodymyrchikh.abitandstudhelp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelectiveGroupRequest {

    @NotBlank(message = "Назва групи є обов'язковою")
    private String name;

    @NotNull(message = "Семестр є обов'язковим")
    @Min(value = 1, message = "Семестр не може бути меншим за 1")
    private Integer semester;

    @NotEmpty(message = "Певні спеціальності є обов'язковими")
    private Set<Long> specialtyIds;
}