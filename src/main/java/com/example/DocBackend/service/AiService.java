package com.example.DocBackend.service;

import com.example.DocBackend.dto.Dto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ── Answer a question about a document ────────────────────────────────────
    public String answerQuestion(String documentText, String question) {
        String prompt = """
                You are a helpful assistant. Use ONLY the document content below to answer the question.
                If the answer is not in the document, say "I could not find that in the document."
                
                DOCUMENT:
                %s
                
                QUESTION: %s
                """.formatted(truncate(documentText, 12000), question);

        log.debug("Sending Q&A prompt to AI");
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    // ── Summarize a document ──────────────────────────────────────────────────
    public String summarize(String documentText) {
        String prompt = """
                Summarize the following content concisely in 3-5 sentences.
                Focus on the main topics and key points.
                
                CONTENT:
                %s
                """.formatted(truncate(documentText, 12000));

        log.debug("Sending summarization prompt to AI");
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    // ── Extract topic timestamps from a transcript ────────────────────────────
    public List<Dto.TopicTimestamp> extractTimestamps(String transcript) {
        String prompt = """
                You are an assistant that extracts topic timestamps from a transcript.
                Analyze the transcript and identify the main topics discussed.
                For each topic, estimate a timestamp in HH:MM:SS format based on the position in the transcript.
                
                Return ONLY a valid JSON array with no extra text. Each object must have:
                - "topic": short topic title
                - "timestamp": estimated time in HH:MM:SS format
                - "description": one sentence about what is discussed
                
                Example:
                [
                  {"topic": "Introduction", "timestamp": "00:00:00", "description": "Speaker introduces the session."},
                  {"topic": "Main Discussion", "timestamp": "00:02:30", "description": "Key points are presented."}
                ]
                
                TRANSCRIPT:
                %s
                """.formatted(truncate(transcript, 10000));

        log.debug("Sending timestamp extraction prompt to AI");
        String raw = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return parseTimestamps(raw);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String truncate(String text, int maxChars) {
        if (text == null) return "";
        return text.length() > maxChars ? text.substring(0, maxChars) + "\n...[truncated]" : text;
    }

    private List<Dto.TopicTimestamp> parseTimestamps(String raw) {
        try {
            // Strip Markdown code fences if present
            String cleaned = raw.replaceAll("(?s)```json\\s*", "").replaceAll("```", "").trim();
            return objectMapper.readValue(cleaned, new TypeReference<List<Dto.TopicTimestamp>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse timestamp JSON: {}", e.getMessage());
            return List.of(new Dto.TopicTimestamp("Parsing Error",
                    "00:00:00", "Could not extract timestamps automatically."));
        }
    }

}
