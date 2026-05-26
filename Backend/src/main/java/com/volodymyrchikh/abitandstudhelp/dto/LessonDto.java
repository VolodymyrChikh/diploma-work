package com.volodymyrchikh.abitandstudhelp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonDto {
    private Long id;
    private String groupName;
    private String academicYear;
    private String semester;
    private String dayOfWeek;
    private Integer pairNumber;
    private String pairLabel;
    private String timeStart;
    private String timeEnd;
    private String weekType;
    private String subjectName;
    private String lessonType;
    private String room;
    // We will parse this back into a list in the UI, or just leave as string. The UI expects teachers to be an array.
    private List<String> teachers; 
    private String rawText;
    private String sourceFile;
}