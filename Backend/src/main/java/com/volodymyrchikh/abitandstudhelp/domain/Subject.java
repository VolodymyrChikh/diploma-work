package com.volodymyrchikh.abitandstudhelp.domain;

import com.volodymyrchikh.abitandstudhelp.common.BlockType;
import com.volodymyrchikh.abitandstudhelp.common.DegreeLevel;
import com.volodymyrchikh.abitandstudhelp.common.ExamType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "subjects")
@EntityListeners(AuditingEntityListener.class)
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Integer credits;
    private String taughtBy;
    private Integer semester;
    private String syllabusLink;
    @Enumerated(EnumType.STRING)
    private BlockType blockType;
    @Enumerated(EnumType.STRING)
    private ExamType examType;
    @Enumerated(EnumType.STRING)
    private DegreeLevel degreeLevel;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id")
    private Specialty specialty;
}
