package com.volodymyrchikh.abitandstudhelp.service.storage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.volodymyrchikh.abitandstudhelp.dto.StorageUploadResponse;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class ObjectStorageService {

    private final S3Client s3Client;
    private final UploadFileValidator uploadFileValidator;

    @Value("${storage.bucket}")
    private String mediaBucket;

    @Value("${storage.schedule-bucket:schedule}")
    private String scheduleBucket;

    @Value("${storage.public-base-url:}")
    private String publicBaseUrl;

    public StorageUploadResponse uploadFile(MultipartFile file) throws IOException {
        FileDetails fileDetails = extractFileDetails(file);
        String datePath = LocalDate.now().toString();
        String key = String.format("media/%s/%s-%s%s", datePath, UUID.randomUUID(), fileDetails.baseName, fileDetails.extension);

        putObject(mediaBucket, key, file.getContentType(), file.getBytes());

        String fileUrl = StorageUrlBuilder.buildFileUrl(publicBaseUrl, mediaBucket, key);
        return new StorageUploadResponse(key, fileUrl);
    }

    public StorageUploadResponse uploadUserAvatar(MultipartFile file, String userName) throws IOException {
        String safeUserName = uploadFileValidator.sanitizeFolderName(userName);
        FileDetails fileDetails = extractFileDetails(file);

        String key = String.format("users/%s/%s-%s%s", safeUserName, UUID.randomUUID(), fileDetails.baseName, fileDetails.extension);

        putObject(mediaBucket, key, file.getContentType(), file.getBytes());

        String fileUrl = StorageUrlBuilder.buildFileUrl(publicBaseUrl, mediaBucket, key);
        return new StorageUploadResponse(key, fileUrl);
    }

    public void uploadScheduleFile(MultipartFile file, String key) throws IOException {
        putObject(scheduleBucket, key, file.getContentType(), file.getBytes());
        String fileUrl = StorageUrlBuilder.buildFileUrl(publicBaseUrl, scheduleBucket, key);
        new StorageUploadResponse(key, fileUrl);
    }

    public void uploadScheduleJson(String json, String key) {
        byte[] payload = json == null ? new byte[0] : json.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        putObject(scheduleBucket, key, "application/json", payload);
        String fileUrl = StorageUrlBuilder.buildFileUrl(publicBaseUrl, scheduleBucket, key);
        new StorageUploadResponse(key, fileUrl);
    }

    public ResponseInputStream<GetObjectResponse> downloadFile(String key) {
        return downloadFromBucket(mediaBucket, key);
    }

    public ResponseInputStream<GetObjectResponse> downloadScheduleFile(String key) {
        return downloadFromBucket(scheduleBucket, key);
    }

    public void deleteFileByKey(String key) {
        if (key == null || key.isBlank()) {
            return;
        }

        deleteByKey(mediaBucket, key);
    }

    public void deleteFileByUrl(String fileUrl) {
        String key = StorageUrlBuilder.tryDecodeKeyFromFileUrl(fileUrl);
        if (key == null) {
            return;
        }

        // If the decoded key already includes the bucket prefix (e.g. "media/...."),
        // strip it before deleting since deleteFileByKey() prepends the configured bucket.
        if (mediaBucket != null && !mediaBucket.isBlank() && key.startsWith(mediaBucket + "/")) {
            key = key.substring(mediaBucket.length() + 1);
        }

        deleteFileByKey(key);
    }

    public void deleteScheduleFileByKey(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        deleteByKey(scheduleBucket, key);
    }

    private void putObject(String targetBucket, String key, String contentType, byte[] payload) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(targetBucket)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(payload));
    }

    private ResponseInputStream<GetObjectResponse> downloadFromBucket(String targetBucket, String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(targetBucket)
                .key(key)
                .build();

        try {
            return s3Client.getObject(request);
        } catch (NoSuchKeyException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Файл не знайдено", ex);
        }
    }

    private void deleteByKey(String targetBucket, String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(targetBucket)
                .key(key)
                .build();

        try {
            s3Client.deleteObject(request);
        } catch (NoSuchKeyException ex) {
            // Object may already be removed; this should not fail user flows.
        }
    }

    private FileDetails extractFileDetails(MultipartFile file) {
        String safeFilename = uploadFileValidator.sanitizeOriginalFilename(file.getOriginalFilename());
        String ext = "";
        String baseName = safeFilename;

        int idx = safeFilename.lastIndexOf('.');
        if (idx > 0) {
            ext = safeFilename.substring(idx);
            baseName = safeFilename.substring(0, idx);
        }

        return new FileDetails(baseName, ext);
    }

    private record FileDetails(String baseName, String extension) {}
}
