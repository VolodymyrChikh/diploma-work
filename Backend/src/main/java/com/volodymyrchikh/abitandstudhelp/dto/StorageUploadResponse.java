package com.volodymyrchikh.abitandstudhelp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StorageUploadResponse {
    private String key;
    private String url;
}

