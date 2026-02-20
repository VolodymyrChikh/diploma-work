package com.volodymyrchikh.abitandstudhelp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequest {

    @NotNull
    private String content;
    @NotNull
    private Long postId;
    @NotNull
    private Long userId;

}