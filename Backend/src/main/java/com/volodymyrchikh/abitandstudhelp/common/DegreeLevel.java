package com.volodymyrchikh.abitandstudhelp.common;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum DegreeLevel {

    BACHELOR("Бакалавр"),
    MASTER("Магістр");

    private final String displayName;

    DegreeLevel(String displayName) {
        this.displayName = displayName;
    }
}