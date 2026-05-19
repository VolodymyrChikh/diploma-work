package com.volodymyrchikh.abitandstudhelp.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MediaResourceRequestTest {

    private final Validator validator;

    MediaResourceRequestTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validationMessagesAreHumanUkrainianText() {
        MediaResourceRequest request = MediaResourceRequest.builder()
                .title("")
                .type(null)
                .categoryId(null)
                .build();

        Set<String> messages = validator.validate(request)
                .stream()
                .map(violation -> violation.getMessage())
                .collect(Collectors.toSet());

        assertTrue(messages.contains("Назва матеріалу не може бути пустою"));
        assertTrue(messages.contains("Тип матеріалу не може бути пустим"));
        assertTrue(messages.contains("Категорія матеріалу не може бути пустою"));
    }
}
