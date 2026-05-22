package com.volodymyrchikh.abitandstudhelp.common;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ExamType {

    EXAM("Іспит"),
    CREDIT("Залік"),
    COURSEWORK("Курсова робота"),
    DIPLOMA("Дипломна робота"),
    MAGISTER("Магістерська робота"),
    DIFFERENTIATED_CREDIT("Диференційований залік"),
    OTHER("Немає");

    private final String displayName;

    ExamType(String displayName) {
        this.displayName = displayName;
    }
}
