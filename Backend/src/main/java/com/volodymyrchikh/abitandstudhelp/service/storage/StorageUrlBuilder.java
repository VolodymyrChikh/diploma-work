package com.volodymyrchikh.abitandstudhelp.service.storage;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class StorageUrlBuilder {

    public static final String FILE_ROUTE = "/api/media/files/";

    private StorageUrlBuilder() {
    }

    public static String buildFileUrl(String publicBaseUrl, String key) {
        String baseUrl = publicBaseUrl == null ? "" : publicBaseUrl.trim();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        // If no public base URL is configured, return a relative backend proxy path.
        if (baseUrl.isBlank()) {
            return FILE_ROUTE + encodeKey(key);
        }

        // If the configured public base URL appears to point directly to an object
        // storage provider (Supabase, S3, GCS, etc.), build a direct storage URL
        // using the raw object key instead of the backend proxy route. This
        // prevents clients from requesting incorrect paths when the env var was
        // set to the storage provider base URL.
        String lower = baseUrl.toLowerCase();
        if (lower.contains("/storage") || lower.contains("supabase") || lower.contains("s3.") || lower.contains("amazonaws.com") || lower.contains("storage.googleapis.com")) {
            return baseUrl + "/" + key;
        }

        return baseUrl + FILE_ROUTE + encodeKey(key);
    }

    public static String buildFileUrl(String publicBaseUrl, String bucket, String key) {
        String baseUrl = publicBaseUrl == null ? "" : publicBaseUrl.trim();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        // Normalize key to always include bucket when building provider URLs
        String fullKey = key == null ? "" : key;
        if (bucket != null && !bucket.isBlank() && !fullKey.startsWith(bucket + "/")) {
            fullKey = bucket + "/" + fullKey;
        }

        // If no public base URL is configured, return a relative backend proxy path.
        if (baseUrl.isBlank()) {
            return FILE_ROUTE + encodeKey(fullKey);
        }

        String lower = baseUrl.toLowerCase();
        if (lower.contains("/storage") || lower.contains("supabase") || lower.contains("s3.") || lower.contains("amazonaws.com") || lower.contains("storage.googleapis.com")) {
            return baseUrl + "/" + fullKey;
        }

        return baseUrl + FILE_ROUTE + encodeKey(fullKey);
    }

    public static String decodeKey(String encodedKey) {
        byte[] decoded = Base64.getUrlDecoder().decode(encodedKey);
        return new String(decoded, StandardCharsets.UTF_8);
    }

    public static String tryDecodeKeyFromFileUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return null;
        }

        String normalizedUrl = fileUrl.trim();
        int routeIndex = normalizedUrl.indexOf(FILE_ROUTE);
        if (routeIndex < 0) {
            // Try to detect direct object storage public URLs (e.g. Supabase/GCS/S3 public paths)
            // Example: https://<project>.supabase.co/storage/v1/object/public/<bucket>/<key>
            int pubIndex = normalizedUrl.indexOf("/object/public/");
            if (pubIndex >= 0) {
                String keyPart = normalizedUrl.substring(pubIndex + "/object/public/".length());
                int queryIndex = keyPart.indexOf('?');
                if (queryIndex >= 0) {
                    keyPart = keyPart.substring(0, queryIndex);
                }
                if (keyPart.isBlank()) {
                    return null;
                }
                return keyPart;
            }

            return null;
        }

        String encodedKey = normalizedUrl.substring(routeIndex + FILE_ROUTE.length());
        int queryIndex = encodedKey.indexOf('?');
        if (queryIndex >= 0) {
            encodedKey = encodedKey.substring(0, queryIndex);
        }

        if (encodedKey.isBlank()) {
            return null;
        }

        try {
            return decodeKey(encodedKey);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static String encodeKey(String key) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(key.getBytes(StandardCharsets.UTF_8));
    }
}
