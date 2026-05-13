package com.example.DocBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class Dto {
    // ── File Upload Response ──────────────────────────────────────────────────
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileUploadResponse {
        private Long id;
        private String originalName;
        private String fileType;
        private Long fileSizeBytes;
        private LocalDateTime uploadedAt;
        private String message;
    }

    // ── File Info Response ────────────────────────────────────────────────────
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class FileInfoResponse {
        private Long id;
        private String originalName;
        private String fileType;
        private String extractedText;
        private String summary;
        private Long fileSizeBytes;
        private LocalDateTime uploadedAt;
    }

    // ── Chat Request / Response ───────────────────────────────────────────────
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ChatRequest {
        private Long fileId;
        private String question;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ChatResponse {
        private Long fileId;
        private String question;
        private String answer;
    }

    // ── Summary Response ──────────────────────────────────────────────────────
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SummaryResponse {
        private Long fileId;
        private String summary;
    }

    // ── Timestamp ─────────────────────────────────────────────────────────────
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TopicTimestamp {
        private String topic;
        private String timestamp;   // e.g. "00:01:24"
        private String description;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TimestampResponse {
        private Long fileId;
        private List<TopicTimestamp> timestamps;
    }
}
