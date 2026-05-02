package com.volodymyrchikh.abitandstudhelp.service.storage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.volodymyrchikh.abitandstudhelp.dto.StorageUploadResponse;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class SupabaseStorageService {

    private final S3Client s3Client;

    @Value("${supabase.storage.bucket}")
    private String bucket;

    @Value("${supabase.storage.public-url-prefix:}")
    private String publicUrlPrefix;

    @Value("${supabase.storage.endpoint:}")
    private String endpoint;

    public StorageUploadResponse uploadFile(MultipartFile file) throws IOException {
        String original = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
        String ext = "";
        int idx = original.lastIndexOf('.');
        if (idx > 0) ext = original.substring(idx);

        String datePath = LocalDate.now().toString(); // e.g. 2026-05-02
        String key = String.format("media/%s/%s-%s%s", datePath, UUID.randomUUID(), sanitize(original), ext);

        PutObjectRequest por = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(por, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        String publicUrl = buildPublicUrl(key);
        return new StorageUploadResponse(key, publicUrl);
    }

    private String buildPublicUrl(String key) {
        if (publicUrlPrefix != null && !publicUrlPrefix.isBlank()) {
            String prefix = publicUrlPrefix.endsWith("/") ? publicUrlPrefix.substring(0, publicUrlPrefix.length() - 1) : publicUrlPrefix;
            return prefix + "/" + bucket + "/" + key;
        }

        String ep = endpoint != null ? endpoint : "";
        if (ep.endsWith("/")) ep = ep.substring(0, ep.length() - 1);
        if (!ep.isEmpty()) {
            // Supabase public object URL should follow:
            // <project>.supabase.co/storage/v1/object/public/<bucket>/<path>
            if (ep.contains("/storage/v1")) {
                return ep + "/object/public/" + bucket + "/" + key;
            }
            return ep + "/storage/v1/object/public/" + bucket + "/" + key;
        }
        // Fallback when no endpoint configured
        return "/storage/v1/object/public/" + bucket + "/" + key;
    }

    private String sanitize(String s) {
        return s.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}




