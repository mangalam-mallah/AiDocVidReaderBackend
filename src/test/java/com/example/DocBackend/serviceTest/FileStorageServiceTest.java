package com.example.DocBackend.serviceTest;

import com.example.DocBackend.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    private FileStorageService service;

    @BeforeEach
    void setUp() throws IOException {
        service = new FileStorageService();
        ReflectionTestUtils.setField(service, "uploadDir", tempDir.toString());
        service.init();
    }

    @Test
    void store_savesFileToDisk() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf",
                "application/pdf", "PDF content".getBytes());

        String storedName = service.store(file);

        assertThat(storedName).endsWith(".pdf");
        assertThat(Files.exists(service.getFilePath(storedName))).isTrue();
    }

    @Test
    void detectFileType_pdf() {
        assertThat(service.detectFileType("document.pdf")).isEqualTo("pdf");
    }

    @Test
    void detectFileType_audio() {
        assertThat(service.detectFileType("song.mp3")).isEqualTo("audio");
        assertThat(service.detectFileType("clip.wav")).isEqualTo("audio");
    }

    @Test
    void detectFileType_video() {
        assertThat(service.detectFileType("movie.mp4")).isEqualTo("video");
        assertThat(service.detectFileType("clip.mov")).isEqualTo("video");
    }

    @Test
    void detectFileType_unknown() {
        assertThat(service.detectFileType("archive.zip")).isEqualTo("unknown");
    }

    @Test
    void delete_removesFile() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "del.pdf",
                "application/pdf", "data".getBytes());
        String stored = service.store(file);
        assertThat(Files.exists(service.getFilePath(stored))).isTrue();

        service.delete(stored);

        assertThat(Files.exists(service.getFilePath(stored))).isFalse();
    }

    public Object detectFileType(String s) {
        return service.detectFileType(s);
    }
}
