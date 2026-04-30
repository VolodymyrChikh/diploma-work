package com.volodymyrchikh.abitandstudhelp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostResponse{

        private Long id;
        private String title;
        private String slug;
        private Integer likes;
        private Boolean isAnonymous;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private CategoryResponse categoryResponse;
        private UserResponse userResponse;

}
