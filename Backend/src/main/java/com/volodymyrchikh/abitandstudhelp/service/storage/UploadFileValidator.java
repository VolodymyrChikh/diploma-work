package com.volodymyrchikh.abitandstudhelp.service.storage;

import com.volodymyrchikh.abitandstudhelp.common.ResourceType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
public class UploadFileValidator {

    public static final int MAX_FILES_PER_UPLOAD = 5;
    public static final long MAX_FILE_SIZE_BYTES = 50L * 1024L * 1024L;

    private static final Set<String> DOCUMENT_EXTENSIONS = Set.of(
            "pdf", "txt", "csv", "rtf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "odt", "ods", "odp"
    );
    private static final Set<String> IMAGE_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "webp", "avif", "bmp", "tif", "tiff", "heic", "heif"
    );
    private static final Set<String> VIDEO_EXTENSIONS = Set.of(
            "mp4", "webm", "ogg", "ogv", "mov", "mpeg", "mpg", "avi"
    );

    private static final Set<String> DOCUMENT_CONTENT_TYPES = Set.of(
            "application/pdf",
            "text/plain",
            "text/csv",
            "application/csv",
            "application/rtf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/vnd.oasis.opendocument.text",
            "application/vnd.oasis.opendocument.spreadsheet",
            "application/vnd.oasis.opendocument.presentation"
    );
    private static final Set<String> IMAGE_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp",
            "image/avif",
            "image/bmp",
            "image/tiff",
            "image/heic",
            "image/heif"
    );
    private static final Set<String> VIDEO_CONTENT_TYPES = Set.of(
            "video/mp4",
            "video/webm",
            "video/ogg",
            "video/quicktime",
            "video/mpeg",
            "video/x-msvideo"
    );

    private static final Map<ResourceType, Set<String>> EXTENSIONS_BY_TYPE = Map.of(
            ResourceType.DOCUMENT, DOCUMENT_EXTENSIONS,
            ResourceType.IMAGE, IMAGE_EXTENSIONS,
            ResourceType.VIDEO, VIDEO_EXTENSIONS,
            ResourceType.VIDEO_LINK, VIDEO_EXTENSIONS
    );

    private static final Map<ResourceType, Set<String>> CONTENT_TYPES_BY_TYPE = Map.of(
            ResourceType.DOCUMENT, DOCUMENT_CONTENT_TYPES,
            ResourceType.IMAGE, IMAGE_CONTENT_TYPES,
            ResourceType.VIDEO, VIDEO_CONTENT_TYPES,
            ResourceType.VIDEO_LINK, VIDEO_CONTENT_TYPES
    );

    public void validateFiles(ResourceType resourceType, List<? extends MultipartFile> files) {
        if (resourceType == null) {
            throw new IllegalArgumentException("Тип матеріалу не може бути пустим");
        }

        if (resourceType == ResourceType.EXTERNAL_LINK) {
            throw new IllegalArgumentException("Зовнішні посилання треба додавати як URL, а не завантажувати файлом");
        }

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Додайте хоча б один файл");
        }

        if (files.size() > MAX_FILES_PER_UPLOAD) {
            throw new IllegalArgumentException("Можна завантажити максимум " + MAX_FILES_PER_UPLOAD + " файлів за раз");
        }

        for (MultipartFile file : files) {
            validateFile(resourceType, file);
        }
    }

    public String sanitizeOriginalFilename(String originalFilename) {
        validateSafeOriginalFilename(originalFilename);

        String filename = originalFilename.replace('\\', '/');
        int lastSlash = filename.lastIndexOf('/');
        if (lastSlash >= 0) {
            filename = filename.substring(lastSlash + 1);
        }

        filename = filename.trim().replaceAll("\\p{Cntrl}", "");
        if (filename.startsWith(".")) {
            filename = "file" + filename;
        }

        String sanitized = filename.replaceAll("[^a-zA-Z0-9._-]", "_");
        sanitized = sanitized.replaceAll("_+", "_");
        if (sanitized.isBlank() || ".".equals(sanitized) || "..".equals(sanitized)) {
            throw new IllegalArgumentException("File name is invalid");
        }

        return sanitized;
    }

    public void validateSafeOriginalFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("File name is required");
        }

        String filename = originalFilename.replace('\\', '/');
        int lastSlash = filename.lastIndexOf('/');
        if (lastSlash >= 0) {
            filename = filename.substring(lastSlash + 1);
        }

        filename = filename.trim();
        if (filename.isBlank() || ".".equals(filename) || "..".equals(filename)) {
            throw new IllegalArgumentException("File name is invalid");
        }

        String extension = extensionOf(filename.startsWith(".") ? "file" + filename : filename);
        if (extension.isBlank()) {
            throw new IllegalArgumentException("File extension is required");
        }
    }

    private void validateFile(ResourceType resourceType, MultipartFile file) {
        if (file == null || file.getSize() <= 0) {
            throw new IllegalArgumentException("Завантажений файл пустий");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("Файл перевищує максимальний розмір 50MB");
        }

        String filename = sanitizeOriginalFilename(file.getOriginalFilename());
        String extension = extensionOf(filename);
        String contentType = normalizeContentType(file.getContentType());

        if (!allowedExtensions(resourceType).contains(extension)) {
            throw new IllegalArgumentException("Розширення файлу не підходить для вибраного матеріалу");
        }

        if (!allowedContentTypes(resourceType).contains(contentType)) {
            throw new IllegalArgumentException("Тип файлу не підходить для вибраного матеріалу");
        }
    }

    private Set<String> allowedExtensions(ResourceType resourceType) {
        Set<String> extensions = EXTENSIONS_BY_TYPE.get(resourceType);
        if (extensions == null) {
            throw new IllegalArgumentException("Завантаження файлів не підтримується для цього типу матеріалу");
        }
        return extensions;
    }

    private Set<String> allowedContentTypes(ResourceType resourceType) {
        Set<String> contentTypes = CONTENT_TYPES_BY_TYPE.get(resourceType);
        if (contentTypes == null) {
            throw new IllegalArgumentException("Завантаження файлів не підтримується для цього типу матеріалу");
        }
        return contentTypes;
    }

    private String extensionOf(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) {
            return "";
        }
        return filename.substring(idx + 1).toLowerCase(Locale.ROOT);
    }

    private String normalizeContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("Тип файлу не може бути пустим");
        }
        return contentType.toLowerCase(Locale.ROOT).split(";", 2)[0].trim();
    }

    public String sanitizeFolderName(String input){
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Folder name is required");
        }

        String sanitized = input.trim().replaceAll("\\p{Cntrl}", "");
        if (sanitized.isBlank() || ".".equals(sanitized) || "..".equals(sanitized)) {
            throw new IllegalArgumentException("Folder name is invalid");
        }

        return sanitized.replaceAll("[^a-zA-Z0-9._-]", "_").replaceAll("_+", "_");
    }
}
