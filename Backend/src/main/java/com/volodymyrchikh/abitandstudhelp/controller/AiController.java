package com.volodymyrchikh.abitandstudhelp.controller;

import com.volodymyrchikh.abitandstudhelp.service.AiService;
import com.volodymyrchikh.abitandstudhelp.dto.ChatMessageDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "*")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/ask")
    public String askAssistant(@RequestParam String message) {
        return aiService.getAnswer(message);
    }

    @PostMapping("/ask")
    public String askAssistant(@RequestBody List<ChatMessageDto> messages) {
        return aiService.getAnswer(messages);
    }
}
