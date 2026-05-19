package com.volodymyrchikh.abitandstudhelp.controller;

import com.volodymyrchikh.abitandstudhelp.dto.FaqRequest;
import com.volodymyrchikh.abitandstudhelp.dto.FaqResponse;
import com.volodymyrchikh.abitandstudhelp.service.FaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/faqs")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    @GetMapping
    public ResponseEntity<List<FaqResponse>> getAllFaqs() {
        return ResponseEntity.ok(faqService.getAllFaqs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FaqResponse> getFaqById(@PathVariable Long id) {
        return ResponseEntity.ok(faqService.getFaqById(id));
    }

    @PostMapping
    public ResponseEntity<FaqResponse> createFaq(@RequestBody FaqRequest request) {
        return new ResponseEntity<>(faqService.createFaq(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FaqResponse> updateFaq(@PathVariable Long id, @RequestBody FaqRequest request) {
        return ResponseEntity.ok(faqService.updateFaq(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaq(@PathVariable Long id) {
        faqService.deleteFaq(id);
        return ResponseEntity.noContent().build();
    }
}
