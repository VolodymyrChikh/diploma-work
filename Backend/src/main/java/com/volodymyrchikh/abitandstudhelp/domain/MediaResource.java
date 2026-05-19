package com.volodymyrchikh.abitandstudhelp.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.volodymyrchikh.abitandstudhelp.common.ResourceType;

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

    @ElementCollection
    @CollectionTable(name = "media_resource_file_urls", joinColumns = @JoinColumn(name = "media_resource_id"))
    @OrderColumn(name = "position")
    @Column(name = "file_url", nullable = false)
    @Builder.Default
    private List<String> fileUrls = new ArrayList<>();

    @Builder.Default
    private Integer views = 0;

    @Enumerated(EnumType.STRING)
    private ResourceType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "file_category_id")
    private FileCategory category;

    @CreatedDate
    private LocalDateTime createdAt;
}