package com.example.DocBackend.controller;


import com.example.DocBackend.dto.Dto;
import com.example.DocBackend.model.UploadedFile;
import com.example.DocBackend.service.AiService;
import com.example.DocBackend.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/summarize")
@RequiredArgsConstructor
public class SummaryController {

    private final FileService fileService;
    private final AiService aiService;

    /**
     * POST /api/summarize/{fileId}
     * Generates a summary of the file content and caches it in the DB.
     */
    @PostMapping("/{fileId}")
    public ResponseEntity<Dto.SummaryResponse> summarize(@PathVariable Long fileId) {
        UploadedFile file = fileService.findById(fileId);

        // Use cached summary if already generated
        if (file.getSummary() != null && !file.getSummary().isBlank()) {
            return ResponseEntity.ok(Dto.SummaryResponse.builder()
                    .fileId(fileId)
                    .summary(file.getSummary())
                    .build());
        }

        String summary = aiService.summarize(file.getExtractedText());
        fileService.saveSummary(fileId, summary);

        return ResponseEntity.ok(Dto.SummaryResponse.builder()
                .fileId(fileId)
                .summary(summary)
                .build());
    }
}
