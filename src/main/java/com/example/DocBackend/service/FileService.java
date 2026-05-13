package com.example.DocBackend.service;

import com.example.DocBackend.dto.Dto;
import com.example.DocBackend.model.UploadedFile;
import com.example.DocBackend.repository.UploadedFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final UploadedFileRepository fileRepository;
    private final FileStorageService storageService;
    private final PdfExtractionService pdfExtractionService;

    // ── Upload ────────────────────────────────────────────────────────────────
    public Dto.FileUploadResponse uploadFile(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
        String fileType = storageService.detectFileType(originalName);

        if (fileType.equals("unknown")) {
            throw new IllegalArgumentException("Unsupported file type: " + originalName);
        }

        // Save to disk
        String storedName = storageService.store(file);
        Path filePath = storageService.getFilePath(storedName);

        // Extract text
        String extractedText = extractText(fileType, filePath);

        // Persist to DB
        UploadedFile entity = UploadedFile.builder()
                .originalName(originalName)
                .storedName(storedName)
                .fileType(fileType)
                .filePath(filePath.toString())
                .extractedText(extractedText)
                .fileSizeBytes(file.getSize())
                .build();
        entity = fileRepository.save(entity);

        log.info("File uploaded and processed: id={} type={}", entity.getId(), fileType);
        return Dto.FileUploadResponse.builder()
                .id(entity.getId())
                .originalName(originalName)
                .fileType(fileType)
                .fileSizeBytes(file.getSize())
                .uploadedAt(entity.getUploadedAt())
                .message("File uploaded and text extracted successfully.")
                .build();
    }

    // ── Get file info ─────────────────────────────────────────────────────────
    public Dto.FileInfoResponse getFileInfo(Long id) {
        UploadedFile file = findById(id);
        return Dto.FileInfoResponse.builder()
                .id(file.getId())
                .originalName(file.getOriginalName())
                .fileType(file.getFileType())
                .extractedText(file.getExtractedText())
                .summary(file.getSummary())
                .fileSizeBytes(file.getFileSizeBytes())
                .uploadedAt(file.getUploadedAt())
                .build();
    }

    // ── List all files ────────────────────────────────────────────────────────
    public List<Dto.FileInfoResponse> listFiles() {
        return fileRepository.findAll().stream()
                .map(f -> Dto.FileInfoResponse.builder()
                        .id(f.getId())
                        .originalName(f.getOriginalName())
                        .fileType(f.getFileType())
                        .fileSizeBytes(f.getFileSizeBytes())
                        .uploadedAt(f.getUploadedAt())
                        .build())
                .toList();
    }

    public void saveSummary(Long id, String summary) {
        UploadedFile file = findById(id);
        file.setSummary(summary);
        fileRepository.save(file);
    }

    public UploadedFile findById(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("File not found: " + id));
    }

    // ── Internal helpers ──────────────────────────────────────────────────────
    private String extractText(String fileType, Path filePath) {
        try {
            return switch (fileType) {
                case "pdf" -> pdfExtractionService.extractText(filePath);
                default -> "";
            };
        } catch (Exception e) {
            log.error("Text extraction failed for {}: {}", filePath, e.getMessage());
            return "";
        }
    }

}
