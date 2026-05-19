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
    @NotBlank
    private String number;
    @NotBlank
    @NotNull
    private String about;

}
