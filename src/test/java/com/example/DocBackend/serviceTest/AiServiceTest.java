package com.example.DocBackend.serviceTest;

import com.example.DocBackend.dto.Dto;
import com.example.DocBackend.service.AiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AiServiceTest {

    @Mock private ChatClient chatClient;
    @Mock private ChatClient.ChatClientRequestSpec requestSpec;
    @Mock private ChatClient.CallResponseSpec callResponseSpec;

    private AiService aiService;

    @BeforeEach
    void setUp() {
        aiService = new AiService(chatClient);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(any(String.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
    }

    @Test
    void answerQuestion_returnsAiResponse() {
        when(callResponseSpec.content()).thenReturn("This document is about Spring Boot.");

        String answer = aiService.answerQuestion("Spring Boot introduction text...", "What is this about?");

        assertThat(answer).isEqualTo("This document is about Spring Boot.");
    }

    @Test
    void summarize_returnsAiResponse() {
        when(callResponseSpec.content()).thenReturn("A concise summary.");

        String summary = aiService.summarize("Long document content...");

        assertThat(summary).isEqualTo("A concise summary.");
    }

    @Test
    void extractTimestamps_parsesValidJson() {
        String json = """
                [
                  {"topic": "Intro", "timestamp": "00:00:00", "description": "Introduction begins."},
                  {"topic": "Main", "timestamp": "00:02:00", "description": "Main content starts."}
                ]
                """;
        when(callResponseSpec.content()).thenReturn(json);

        List<Dto.TopicTimestamp> result = aiService.extractTimestamps("Some transcript text...");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTopic()).isEqualTo("Intro");
        assertThat(result.get(1).getTimestamp()).isEqualTo("00:02:00");
    }

    @Test
    void extractTimestamps_handlesInvalidJson_gracefully() {
        when(callResponseSpec.content()).thenReturn("not valid json at all");

        List<Dto.TopicTimestamp> result = aiService.extractTimestamps("transcript...");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTopic()).isEqualTo("Parsing Error");
    }

    @Test
    void extractTimestamps_stripsMarkdownFences() {
        String fenced = """
                ```json
                [{"topic":"T","timestamp":"00:01:00","description":"Desc."}]
                ```
                """;
        when(callResponseSpec.content()).thenReturn(fenced);

        List<Dto.TopicTimestamp> result = aiService.extractTimestamps("text");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTopic()).isEqualTo("T");
    }

}
