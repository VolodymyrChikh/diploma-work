package com.volodymyrchikh.abitandstudhelp.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "app.gemini")
public class GeminiProperties {
    private String apiKey;
    private String embeddingModel = "gemini-embedding-001";
    private int embeddingDimension = 768;
    private String baseUrl = "https://generativelanguage.googleapis.com/v1beta";

}

