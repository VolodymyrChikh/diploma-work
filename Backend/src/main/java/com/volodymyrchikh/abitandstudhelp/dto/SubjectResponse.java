package com.volodymyrchikh.abitandstudhelp.dto;

import com.volodymyrchikh.abitandstudhelp.common.BlockType;
import com.volodymyrchikh.abitandstudhelp.common.DegreeLevel;
import com.volodymyrchikh.abitandstudhelp.common.ExamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubjectResponse {

    private Long id;
    private String name;
    private String description;
    private Integer credits;
    private String taughtBy;
    private Integer semester;
    private String syllabusLink;
    private String specialtyName;
    private BlockType blockType;
    private ExamType examType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long specialtyId;
    private Long selectiveGroupId;
    private DegreeLevel degreeLevel;

}

