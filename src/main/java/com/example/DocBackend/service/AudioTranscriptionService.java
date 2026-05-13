package com.example.DocBackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import java.nio.file.Path;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AudioTranscriptionService {
    private final ChatClient chatClient;

    // Maps file extension → MIME type accepted by Gemini
    private static final Map<String, String> MIME_TYPES = Map.of(
            "mp3",  "audio/mpeg",
            "wav",  "audio/wav",
            "m4a",  "audio/mp4",
            "ogg",  "audio/ogg",
            "flac", "audio/flac",
            "mp4",  "video/mp4",
            "mov",  "video/quicktime",
            "avi",  "video/x-msvideo",
            "mkv",  "video/x-matroska",
            "webm", "video/webm"
    );

    /**
     * Transcribes an audio or video file using Gemini 1.5 Pro's multimodal capability.
     * Gemini can directly process audio and video — no separate ASR service needed.
     */
    public String transcribe(Path filePath) {
        log.info("Transcribing file with Gemini: {}", filePath.getFileName());

        String extension = getExtension(filePath.getFileName().toString());
        String mimeTypeStr = MIME_TYPES.getOrDefault(extension, "audio/mpeg");
        MimeType mimeType = MimeTypeUtils.parseMimeType(mimeTypeStr);

        String transcript = chatClient.prompt()
                .user(u -> u
                        .text("""
                                Please transcribe this audio/video file completely and accurately.
                                Return only the transcription text, no commentary or formatting.
                                If there are multiple speakers, prefix each line with 'Speaker X:'.
                                """)
                        .media(mimeType, new FileSystemResource(filePath.toFile()))
                )
                .call()
                .content();

        log.info("Transcription complete: {} characters", transcript.length());
        return transcript;
    }

    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot >= 0) ? filename.substring(dot + 1).toLowerCase() : "";
    }
}
