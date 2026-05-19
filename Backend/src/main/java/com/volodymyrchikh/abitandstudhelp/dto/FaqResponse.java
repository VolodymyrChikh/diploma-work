package com.volodymyrchikh.abitandstudhelp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FaqResponse {
    private Long id;
    private String question;
    private String answer;
}
