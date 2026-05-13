package com.example.DocBackend.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(Paths.get(uploadDir));
        log.info("Upload directory ready: {}", uploadDir);
    }

    /**
     * Saves a multipart file to disk and returns the stored filename.
     */
    public String store(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename() != null
                ? file.getOriginalFilename() : "unknown";
        String extension = getExtension(originalName);
        String storedName = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);

        Path destination = Paths.get(uploadDir).resolve(storedName);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        log.info("Stored file {} as {}", originalName, storedName);
        return storedName;
    }

    public Path getFilePath(String storedName) {
        return Paths.get(uploadDir).resolve(storedName);
    }

    public void delete(String storedName) throws IOException {
        Path path = Paths.get(uploadDir).resolve(storedName);
        Files.deleteIfExists(path);
    }

    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot >= 0) ? filename.substring(dot + 1).toLowerCase() : "";
    }

    public String detectFileType(String filename) {
        String ext = getExtension(filename);
        return switch (ext) {
            case "pdf" -> "pdf";
            case "mp3", "wav", "m4a", "ogg", "flac" -> "audio";
            case "mp4", "mov", "avi", "mkv", "webm" -> "video";
            default -> "unknown";
        };
    }
}
