package com.volodymyrchikh.abitandstudhelp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.volodymyrchikh.abitandstudhelp.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public JsonNode uploadSchedule(@RequestParam("file") MultipartFile file) throws java.io.IOException {
        return scheduleService.uploadSchedule(file);
    }

    @GetMapping("/latest")
    public JsonNode getLatestSchedule() throws java.io.IOException {
        return scheduleService.getLatestSchedule();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @org.springframework.web.bind.annotation.DeleteMapping("/latest")
    public JsonNode deleteLatestSchedule(@RequestParam(value = "sourceFile", required = false) String sourceFile) throws java.io.IOException {
        if (sourceFile != null && !sourceFile.isBlank()) {
            return scheduleService.deleteScheduleBySourceFile(sourceFile);
        } else {
            scheduleService.deleteLatestSchedule();
            return new com.fasterxml.jackson.databind.ObjectMapper().createArrayNode();
        }
    }
}
