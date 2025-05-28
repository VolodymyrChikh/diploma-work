package com.volodymyrchikh.abitandstudhelp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SpecialtyRequest {

    @NotNull
    @NotBlank
    private String name;
    @NotNull
    private Integer number;
    @NotBlank
    @NotNull
    private String about;

}
