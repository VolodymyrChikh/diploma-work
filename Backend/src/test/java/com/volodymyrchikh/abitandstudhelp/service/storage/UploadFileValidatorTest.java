package com.volodymyrchikh.abitandstudhelp.service.storage;

import com.volodymyrchikh.abitandstudhelp.common.ResourceType;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UploadFileValidatorTest {

    private final UploadFileValidator validator = new UploadFileValidator();

    @Test
    void acceptsExpectedDocumentImageAndVideoFiles() {
        assertDoesNotThrow(() -> validator.validateFiles(ResourceType.DOCUMENT, List.of(
                file("syllabus.pdf", "application/pdf", 1024)
        )));
        assertDoesNotThrow(() -> validator.validateFiles(ResourceType.IMAGE, List.of(
                file("campus.jpg", "image/jpeg", 1024)
        )));
        assertDoesNotThrow(() -> validator.validateFiles(ResourceType.VIDEO_LINK, List.of(
                file("lecture.mp4", "video/mp4", 1024)
        )));
    }

    @Test
    void rejectsMissingFiles() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateFiles(ResourceType.DOCUMENT, List.of()));
    }

    @Test
    void rejectsTooManyFiles() {
        List<MockMultipartFile> files = List.of(
                file("1.pdf", "application/pdf", 1),
                file("2.pdf", "application/pdf", 1),
                file("3.pdf", "application/pdf", 1),
                file("4.pdf", "application/pdf", 1),
                file("5.pdf", "application/pdf", 1),
                file("6.pdf", "application/pdf", 1)
        );

        assertThrows(IllegalArgumentException.class, () -> validator.validateFiles(ResourceType.DOCUMENT, files));
    }

    @Test
    void rejectsFilesOverFiftyMegabytes() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateFiles(ResourceType.DOCUMENT, List.of(
                file("huge.pdf", "application/pdf", 50L * 1024L * 1024L + 1L)
        )));
    }

    @Test
    void rejectsExecutableOrHtmlUploadsEvenWhenLabeledAsDocuments() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateFiles(ResourceType.DOCUMENT, List.of(
                file("payload.exe", "application/octet-stream", 1024)
        )));
        assertThrows(IllegalArgumentException.class, () -> validator.validateFiles(ResourceType.DOCUMENT, List.of(
                file("payload.html", "text/html", 1024)
        )));
    }

    @Test
    void rejectsSvgImagesBecauseTheyCanContainActiveContent() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateFiles(ResourceType.IMAGE, List.of(
                file("payload.svg", "image/svg+xml", 1024)
        )));
    }

    @Test
    void rejectsMismatchedExtensionAndContentType() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateFiles(ResourceType.IMAGE, List.of(
                file("photo.jpg", "application/pdf", 1024)
        )));
    }

    @Test
    void rejectsExternalLinkUploadsOnMultipartEndpoint() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validateFiles(ResourceType.EXTERNAL_LINK, List.of(
                file("link.txt", "text/plain", 1024)
        )));

        assertEquals("Зовнішні посилання треба додавати як URL, а не завантажувати файлом", exception.getMessage());
    }

    @Test
    void rejectsBadUploadInputWithUkrainianMessages() {
        assertEquals(
                "Тип матеріалу не може бути пустим",
                assertThrows(IllegalArgumentException.class, () -> validator.validateFiles(null, List.of(
                        file("file.pdf", "application/pdf", 1024)
                ))).getMessage()
        );
        assertEquals(
                "Додайте хоча б один файл",
                assertThrows(IllegalArgumentException.class, () -> validator.validateFiles(ResourceType.DOCUMENT, List.of()))
                        .getMessage()
        );
        assertEquals(
                "Файл перевищує максимальний розмір 50MB",
                assertThrows(IllegalArgumentException.class, () -> validator.validateFiles(ResourceType.DOCUMENT, List.of(
                        file("huge.pdf", "application/pdf", 50L * 1024L * 1024L + 1L)
                ))).getMessage()
        );
        assertEquals(
                "Тип файлу не підходить для вибраного матеріалу",
                assertThrows(IllegalArgumentException.class, () -> validator.validateFiles(ResourceType.IMAGE, List.of(
                        file("photo.jpg", "application/pdf", 1024)
                ))).getMessage()
        );
    }

    @Test
    void sanitizesFileNamesWithoutPathSegmentsOrHiddenNames() {
        assertDoesNotThrow(() -> validator.validateSafeOriginalFilename("../folder/.env"));
        assertDoesNotThrow(() -> validator.validateSafeOriginalFilename("курс план 2026.pdf"));
    }

    @Test
    void rejectsBlankOrExtensionlessFileNames() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateSafeOriginalFilename(""));
        assertThrows(IllegalArgumentException.class, () -> validator.validateSafeOriginalFilename("README"));
    }

    private MockMultipartFile file(String filename, String contentType, long size) {
        return new MockMultipartFile("files", filename, contentType, new byte[0]) {
            @Override
            public long getSize() {
                return size;
            }
        };
    }
}
