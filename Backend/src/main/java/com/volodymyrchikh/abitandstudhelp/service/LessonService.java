package com.volodymyrchikh.abitandstudhelp.service;

import com.volodymyrchikh.abitandstudhelp.domain.Lesson;
import com.volodymyrchikh.abitandstudhelp.dto.LessonDto;
import com.volodymyrchikh.abitandstudhelp.mapper.LessonMapper;
import com.volodymyrchikh.abitandstudhelp.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<LessonDto> getLessonsByGroup(String groupName) {
        if (groupName == null || groupName.isBlank()) {
            return List.of();
        }

        String target = normalizeGroupName(groupName);

        return lessonRepository.findAll().stream()
                .filter(l -> {
                    String lg = l.getGroupName();
                    if (lg == null) return false;
                    return normalizeGroupName(lg).equals(target);
                })
                .map(lessonMapper::mapToResponse)
                .toList();
    }

    private String normalizeGroupName(String raw) {
        if (raw == null) return "";
        String s = raw.trim().toUpperCase();
        // normalize various dash types to ASCII hyphen
        s = s.replace('\u2013', '-').replace('\u2014', '-').replace('\u2212', '-');
        // replace any sequence of whitespace around hyphen with single hyphen
        s = s.replaceAll("\\s*[-–—\\u2013\\u2014\\u2212]\\s*", "-");
        // remove any characters that are not letters, digits or hyphen
        s = s.replaceAll("[^\\p{L}\\p{Nd}-]", "");
        // if there are trailing letters after digits (like '11С'), remove them
        s = s.replaceAll("(?<=\\d)[\\p{L}]+$", "");
        // trim possible leading/trailing hyphens
        s = s.replaceAll("(^-+|-+$)", "");
        return s;
    }

    @Transactional(readOnly = true)
    public List<String> getAvailableGroups() {
        return lessonRepository.findDistinctGroupNames();
    }

    @Transactional
    public LessonDto createLesson(LessonDto createDto) {
        Lesson lesson = new Lesson();
        lesson.setGroupName(createDto.getGroupName());
        lesson.setAcademicYear(createDto.getAcademicYear());
        lesson.setSemester(createDto.getSemester());
        lesson.setSubjectName(createDto.getSubjectName());
        lesson.setLessonType(createDto.getLessonType());
        lesson.setRoom(createDto.getRoom());
        
        try {
            lesson.setTeachers(createDto.getTeachers() != null ? objectMapper.writeValueAsString(createDto.getTeachers()) : "[]");
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            lesson.setTeachers("[]");
        }
        
        lesson.setTimeStart(createDto.getTimeStart());
        lesson.setTimeEnd(createDto.getTimeEnd());
        lesson.setWeekType(createDto.getWeekType());
        lesson.setPairNumber(createDto.getPairNumber());
        lesson.setPairLabel(createDto.getPairLabel());
        lesson.setDayOfWeek(createDto.getDayOfWeek());
        lesson.setSourceFile(createDto.getSourceFile());

        return lessonMapper.mapToResponse(lessonRepository.save(lesson));
    }

    @Transactional
    public LessonDto updateLesson(Long id, LessonDto updateDto) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));

        lesson.setSubjectName(updateDto.getSubjectName());
        lesson.setLessonType(updateDto.getLessonType());
        lesson.setRoom(updateDto.getRoom());
        
        try {
            lesson.setTeachers(updateDto.getTeachers() != null ? objectMapper.writeValueAsString(updateDto.getTeachers()) : "[]");
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            lesson.setTeachers("[]");
        }
        
        lesson.setTimeStart(updateDto.getTimeStart());
        lesson.setTimeEnd(updateDto.getTimeEnd());
        lesson.setWeekType(updateDto.getWeekType());
        lesson.setPairNumber(updateDto.getPairNumber());
        lesson.setPairLabel(updateDto.getPairLabel());
        lesson.setDayOfWeek(updateDto.getDayOfWeek());

        return lessonMapper.mapToResponse(lessonRepository.save(lesson));
    }

    @Transactional
    public void deleteLesson(Long id) {
        if (!lessonRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found");
        }
        lessonRepository.deleteById(id);
    }
}