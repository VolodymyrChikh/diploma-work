package com.volodymyrchikh.abitandstudhelp.service.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StorageUrlBuilderTest {

    @Test
    void buildsDownloadUrlFromPublicBackendUrlAndObjectKey() {
        String key = "media/2026-05-05/document file.pdf";

        String url = StorageUrlBuilder.buildFileUrl("https://api.example.com/", key);

        String encodedKey = url.substring(url.lastIndexOf('/') + 1);
        assertEquals("https://api.example.com/api/media/files/" + encodedKey, url);
        assertEquals(key, StorageUrlBuilder.decodeKey(encodedKey));
    }

    @Test
    void normalizesMissingPublicBackendUrlToRelativePath() {
        String key = "media/2026-05-05/image.png";

        String url = StorageUrlBuilder.buildFileUrl("", key);

        String encodedKey = url.substring(url.lastIndexOf('/') + 1);
        assertEquals("/api/media/files/" + encodedKey, url);
        assertEquals(key, StorageUrlBuilder.decodeKey(encodedKey));
    }
}
