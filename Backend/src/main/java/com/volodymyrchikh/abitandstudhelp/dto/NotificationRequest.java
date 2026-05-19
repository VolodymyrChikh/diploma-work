package com.volodymyrchikh.abitandstudhelp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRequest {

    private String type;
    private String message;
    private Boolean isRead;
    private Long userId;

}
