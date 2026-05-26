package com.volodymyrchikh.abitandstudhelp.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.volodymyrchikh.abitandstudhelp.domain.Lesson;
import com.volodymyrchikh.abitandstudhelp.dto.LessonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LessonMapper {
    
    private final ObjectMapper objectMapper;

    public LessonDto mapToResponse(Lesson lesson) {
        return LessonDto.builder()
                .id(lesson.getId())
                .groupName(lesson.getGroupName())
                .academicYear(lesson.getAcademicYear())
                .semester(lesson.getSemester())
                .dayOfWeek(lesson.getDayOfWeek())
                .pairNumber(lesson.getPairNumber())
                .pairLabel(lesson.getPairLabel())
                .timeStart(lesson.getTimeStart())
                .timeEnd(lesson.getTimeEnd())
                .weekType(lesson.getWeekType())
                .subjectName(lesson.getSubjectName())
                .lessonType(lesson.getLessonType())
                .room(lesson.getRoom())
                .teachers(parseTeachers(lesson.getTeachers()))
                .rawText(lesson.getRawText())
                .sourceFile(lesson.getSourceFile())
                .build();
    }

    private List<String> parseTeachers(String teachersJson) {
        if (teachersJson == null || teachersJson.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(teachersJson, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return List.of(teachersJson.split(","));
        }
    }
}