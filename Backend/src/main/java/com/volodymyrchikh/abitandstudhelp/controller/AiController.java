package com.volodymyrchikh.abitandstudhelp.controller;

import com.volodymyrchikh.abitandstudhelp.service.AiService;
import org.springframework.web.bind.annotation.*;

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
}


