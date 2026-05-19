package com.volodymyrchikh.abitandstudhelp.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    SYSTEM("System"),                               // Загальні системні сповіщення
    ADMIN_MESSAGE("Admin Message"),                 // Повідомлення від адміністратора користувачу
    BEHAVIOR_WARNING("Behavior Warning"),           // Сповіщення про погану поведінку
    NEW_REPLY("New Reply"),                         // Сповіщення про нову відповідь на коментар
    CONTENT_REPORTED("Content Reported"),           // Сповіщення для адміністратора про скаргу на контент
    INAPPROPRIATE_CONTENT("Inappropriate Content"); // Сповіщення про неприйнятний контент

    private final String name;

    @JsonCreator
    public static NotificationType forValue(String value) {
        return Arrays.stream(NotificationType.values())
                .filter(notificationType -> notificationType.getName().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Notification Type: " + value));
    }

    @JsonValue
    public String getName() {
        return name;
    }
}