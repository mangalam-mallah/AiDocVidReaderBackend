package com.example.DocBackend.controller;

import com.example.DocBackend.dto.Dto;
import com.example.DocBackend.model.UploadedFile;
import com.example.DocBackend.service.AiService;
import com.example.DocBackend.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/timestamps")
@RequiredArgsConstructor
public class TimeStampController {

    private final FileService fileService;
    private final AiService aiService;

    /**
     * GET /api/timestamps/{fileId}
     * Returns topic timestamps for audio or video files.
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<Dto.TimestampResponse> getTimestamps(@PathVariable Long fileId) {
        UploadedFile file = fileService.findById(fileId);

        if (!file.getFileType().equals("audio") && !file.getFileType().equals("video")) {
            return ResponseEntity.badRequest().build();
        }

        List<Dto.TopicTimestamp> timestamps = aiService.extractTimestamps(file.getExtractedText());

        return ResponseEntity.ok(Dto.TimestampResponse.builder()
                .fileId(fileId)
                .timestamps(timestamps)
                .build());
    }
}
