package com.volodymyrchikh.abitandstudhelp.service;

import com.volodymyrchikh.abitandstudhelp.domain.Lesson;
import com.volodymyrchikh.abitandstudhelp.repository.LessonRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.volodymyrchikh.abitandstudhelp.common.ResourceType;
import com.volodymyrchikh.abitandstudhelp.service.storage.ObjectStorageService;
import com.volodymyrchikh.abitandstudhelp.service.storage.UploadFileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private static final String LATEST_SCHEDULE_KEY = "latest/schedule.json";

    private final ObjectStorageService objectStorageService;
    private final UploadFileValidator uploadFileValidator;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final LessonRepository lessonRepository;

    @Value("${schedule.parser-url}")
    private String parserUrl;

    @org.springframework.transaction.annotation.Transactional
    public JsonNode uploadSchedule(MultipartFile file) throws IOException {
        uploadFileValidator.validateFiles(ResourceType.DOCUMENT, List.of(file));

        String originalFilename = uploadFileValidator.sanitizeOriginalFilename(file.getOriginalFilename());
        if (!originalFilename.toLowerCase(java.util.Locale.ROOT).endsWith(".pdf")) {
            throw new IllegalArgumentException("Потрібно завантажити PDF-файл");
        }

        String scheduleFolder = resolveScheduleFolder(LocalDate.now());
        String pdfId = UUID.randomUUID().toString();
        String pdfKey = scheduleFolder + pdfId + "-" + originalFilename;
        String jsonKey = scheduleFolder + pdfId + "-schedule.json";

        String parsedJson = parseScheduleWithHelper(file);
        
        objectStorageService.uploadScheduleFile(file, pdfKey);
        objectStorageService.uploadScheduleJson(parsedJson, jsonKey);

        JsonNode newDoc = objectMapper.readTree(parsedJson);

        ArrayNode schedulesNode;
        try {
            JsonNode latest = getLatestSchedule();
            if (latest.isArray()) {
                schedulesNode = (ArrayNode) latest;
            } else {
                schedulesNode = objectMapper.createArrayNode();
                schedulesNode.add(latest);
            }
        } catch (org.springframework.web.server.ResponseStatusException e) {
            schedulesNode = objectMapper.createArrayNode();
        }

        schedulesNode.add(newDoc);

        objectStorageService.uploadScheduleJson(schedulesNode.toString(), LATEST_SCHEDULE_KEY);

        saveLessonsToDatabase(newDoc);

        return schedulesNode;
    }

    public JsonNode getLatestSchedule() throws IOException {
        try (var response = objectStorageService.downloadScheduleFile(LATEST_SCHEDULE_KEY)) {
            byte[] payload = response.readAllBytes();
            if (payload.length == 0) {
                throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Розклад не знайдено");
            }
            JsonNode node = objectMapper.readTree(payload);
            if (!node.isArray()) {
                ArrayNode arr = objectMapper.createArrayNode();
                arr.add(node);
                return arr;
            }
            return node;
        }
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteLatestSchedule() {
        objectStorageService.deleteScheduleFileByKey(LATEST_SCHEDULE_KEY);
        lessonRepository.deleteAll();
    }

    @org.springframework.transaction.annotation.Transactional
    public JsonNode deleteScheduleBySourceFile(String sourceFile) throws IOException {
        JsonNode latest = getLatestSchedule();
        if (!latest.isArray()) {
            return latest;
        }

        ArrayNode updatedArray = objectMapper.createArrayNode();
        boolean removed = false;

        for (JsonNode node : latest) {
            String nodeSourceFile = node.path("source_file").asText("");
            if (nodeSourceFile.equals(sourceFile)) {
                removed = true;
            } else {
                updatedArray.add(node);
            }
        }

        if (removed) {
            lessonRepository.deleteBySourceFile(sourceFile);
            if (updatedArray.isEmpty()) {
                deleteLatestSchedule();
            } else {
                objectStorageService.uploadScheduleJson(updatedArray.toString(), LATEST_SCHEDULE_KEY);
            }
        }

        return updatedArray;
    }

    private String parseScheduleWithHelper(MultipartFile file) throws IOException {
        byte[] payload = file.getBytes();
        ByteArrayResource resource = new ByteArrayResource(payload) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        String response = restTemplate.postForObject(parserUrl, request, String.class);
        if (response == null || response.isBlank()) {
            throw new IllegalStateException("Порожня відповідь від сервісу розбору розкладу");
        }
        return response;
    }

    private void saveLessonsToDatabase(JsonNode parsedDocNode) {
        String academicYear = parsedDocNode.path("academic_year").asText(null);
        String semester = parsedDocNode.path("semester").asText(null);
        String sourceFile = parsedDocNode.path("source_file").asText(null);

        JsonNode entries = parsedDocNode.path("entries");
        if (entries.isArray()) {
            java.util.List<Lesson> lessons = new java.util.ArrayList<>();
            for (JsonNode entry : entries) {
                JsonNode groupsNode = entry.path("groups");
                if (groupsNode.isArray()) {
                    for (JsonNode groupNode : groupsNode) {
                        String groupName = groupNode.asText();
                        Lesson lesson = Lesson.builder()
                                .groupName(groupName)
                                .academicYear(academicYear)
                                .semester(semester)
                                .dayOfWeek(entry.path("day").asText())
                                .pairNumber(entry.path("pair_number").isNull() ? null : entry.path("pair_number").asInt())
                                .pairLabel(entry.path("pair_label").asText(null))
                                .timeStart(entry.path("time_start").asText(null))
                                .timeEnd(entry.path("time_end").asText(null))
                                .weekType(entry.path("week_type").asText("ALL"))
                                .subjectName(entry.path("subject").asText(null))
                                .lessonType(entry.path("lesson_type").asText(null))
                                .room(entry.path("room").asText(null))
                                .teachers(entry.path("teachers").toString())
                                .rawText(entry.path("raw_text").asText(null))
                                .sourceFile(sourceFile)
                                .build();
                        lessons.add(lesson);
                    }
                }
            }
            lessonRepository.saveAll(lessons);
        }
    }

    private String resolveScheduleFolder(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();

        if (month <= 6) {
            return (year - 1) + "-" + year + "/semester2/";
        }

        return year + "-" + (year + 1) + "/semester1/";
    }
}
