package com.volodymyrchikh.abitandstudhelp.controller;

import com.volodymyrchikh.abitandstudhelp.dto.LessonDto;
import com.volodymyrchikh.abitandstudhelp.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping
    public List<LessonDto> getLessons(@RequestParam String groupName) {
        return lessonService.getLessonsByGroup(groupName);
    }

    @GetMapping("/groups")
    public List<String> getAvailableGroups() {
        return lessonService.getAvailableGroups();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public LessonDto createLesson(@RequestBody LessonDto lessonDto) {
        return lessonService.createLesson(lessonDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public LessonDto updateLesson(@PathVariable Long id, @RequestBody LessonDto lessonDto) {
        return lessonService.updateLesson(id, lessonDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
    }
}