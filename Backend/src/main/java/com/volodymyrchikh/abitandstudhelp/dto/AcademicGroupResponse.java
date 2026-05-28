package com.volodymyrchikh.abitandstudhelp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AcademicGroupResponse {

    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

