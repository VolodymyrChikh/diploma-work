package com.volodymyrchikh.abitandstudhelp.exception;

import lombok.Getter;

@Getter
public class NotificationNotFoundException extends RuntimeException {

    private final Long notificationId;

    public NotificationNotFoundException(String message, Long notificationId) {
        super(message);
        this.notificationId = notificationId;
    }
}
