package com.volodymyrchikh.abitandstudhelp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpecialtyResponse {

    private Long id;
    private String name;
    private String number;
    private String about;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}