package com.volodymyrchikh.abitandstudhelp.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelectiveGroupResponse {
    private Long id;
    private String name;
    private Integer semester;
    private Set<Long> specialtyIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}