package com.volodymyrchikh.abitandstudhelp.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name", nullable = false, length = 100)
    private String groupName;

    @Column(name = "academic_year", length = 20)
    private String academicYear;

    @Column(length = 10)
    private String semester;

    @Column(name = "day_of_week", nullable = false, length = 20)
    private String dayOfWeek;

    @Column(name = "pair_number")
    private Integer pairNumber;

    @Column(name = "pair_label", length = 10)
    private String pairLabel;

    @Column(name = "time_start", length = 10)
    private String timeStart;

    @Column(name = "time_end", length = 10)
    private String timeEnd;

    @Column(name = "week_type", nullable = false, length = 20)
    private String weekType;

    @Column(name = "subject_name")
    private String subjectName;

    @Column(name = "lesson_type", length = 50)
    private String lessonType;

    @Column(length = 50)
    private String room;

    @Column(columnDefinition = "TEXT")
    private String teachers;

    @Column(name = "raw_text", columnDefinition = "TEXT")
    private String rawText;

    @Column(name = "source_file", length = 255)
    private String sourceFile;
}