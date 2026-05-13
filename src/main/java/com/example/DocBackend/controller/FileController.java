package com.example.DocBackend.controller;

import com.example.DocBackend.dto.Dto;
import com.example.DocBackend.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    /**
     * POST /api/files/upload
     * Accepts PDF, audio, or video. Extracts text/transcript automatically.
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Dto.FileUploadResponse> upload(@RequestParam("file") MultipartFile file) throws IOException {
        Dto.FileUploadResponse response = fileService.uploadFile(file);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/files/{id}
     * Returns file metadata + extracted text.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Dto.FileInfoResponse> getFile(@PathVariable Long id) {
        return ResponseEntity.ok(fileService.getFileInfo(id));
    }

    /**
     * GET /api/files
     * Lists all uploaded files (metadata only, no text).
     */
    @GetMapping
    public ResponseEntity<List<Dto.FileInfoResponse>> listFiles() {
        return ResponseEntity.ok(fileService.listFiles());
    }
}
