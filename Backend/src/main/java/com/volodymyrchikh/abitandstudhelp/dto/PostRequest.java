package com.volodymyrchikh.abitandstudhelp.dto;

import lombok.Data;

@Data
public class PostRequest {

    private String title;
    private Integer likes;
    private Boolean isAnonymous;
    private Long categoryId;
    private Long userId;

}
