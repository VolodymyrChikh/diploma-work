package com.volodymyrchikh.abitandstudhelp.dto;

import com.volodymyrchikh.abitandstudhelp.common.BlockType;
import com.volodymyrchikh.abitandstudhelp.common.DegreeLevel;
import com.volodymyrchikh.abitandstudhelp.common.ExamType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubjectRequest {

    @NotBlank
    private String name;
    private String description;
    private Integer credits;
    private String taughtBy;
    private Integer semester;
    private String syllabusLink;
    @NotNull
    private Long specialtyId;
    @NotNull
    private BlockType blockType;
    @NotNull
    private ExamType examType;
    private DegreeLevel degreeLevel;

}

