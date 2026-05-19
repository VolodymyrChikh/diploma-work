package com.volodymyrchikh.abitandstudhelp.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Specialty {

    SECONDARY_EDUCATION_INFORMATICS("Середня освіта (Інформатика)"),
    APPLIED_MATHEMATICS("Прикладна математика"),
    COMPUTER_SCIENCES("Комп’ютерні науки"),
    SYSTEM_ANALYSIS("Системний аналіз"),
    CYBERSECURITY("Кібербезпека та захист інформації");

    private final String name;

    @JsonCreator
    public static Specialty forValue(String value) {
        return Arrays.stream(Specialty.values())
                .filter(specialty -> specialty.getName().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown specialty: " + value));
    }

    @JsonValue
    public String getName() {
        return name;
    }

}
