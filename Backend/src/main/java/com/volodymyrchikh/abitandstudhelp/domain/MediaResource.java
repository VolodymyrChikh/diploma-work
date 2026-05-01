package com.volodymyrchikh.abitandstudhelp.domain;

import jakarta.persistence.*;
import lombok.*;
import com.volodymyrchikh.abitandstudhelp.common.ResourceType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "media_resources")
@EntityListeners(AuditingEntityListener.class)
public class MediaResource {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;
    private String description;
    private String url;

    @Builder.Default
    private Integer views = 0;

    @Enumerated(EnumType.STRING)
    private ResourceType type;

    @ManyToOne
    private Category category;

    @CreatedDate
    private LocalDateTime createdAt;
}