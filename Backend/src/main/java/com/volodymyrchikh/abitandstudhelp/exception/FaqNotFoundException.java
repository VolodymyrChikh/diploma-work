package com.volodymyrchikh.abitandstudhelp.exception;

import lombok.Getter;

@Getter
public class FaqNotFoundException extends RuntimeException {

    private final Long faqId;

    public FaqNotFoundException(String message, Long faqId) {
        super(message);
        this.faqId = faqId;
    }
}
