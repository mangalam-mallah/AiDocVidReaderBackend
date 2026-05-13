package com.example.DocBackend.serviceTest;

import com.example.DocBackend.service.AudioTranscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.io.IOException;
import java.nio.file.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AudioTranscriptionServiceTest {

    @Mock private ChatClient chatClient;
    @Mock private ChatClient.ChatClientRequestSpec requestSpec;
    @Mock private ChatClient.CallResponseSpec callResponseSpec;

    @TempDir
    Path tempDir;

    private AudioTranscriptionService service;

    @BeforeEach
    void setUp() {
        service = new AudioTranscriptionService(chatClient);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(any(java.util.function.Consumer.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
    }

    @Test
    void transcribe_mp3_returnsTranscript() throws IOException {
        Path audioFile = tempDir.resolve("test.mp3");
        Files.write(audioFile, "fake audio bytes".getBytes());

        when(callResponseSpec.content()).thenReturn("Hello, this is the transcribed text.");

        String result = service.transcribe(audioFile);

        assertThat(result).isEqualTo("Hello, this is the transcribed text.");
        verify(chatClient).prompt();
    }

    @Test
    void transcribe_mp4_returnsTranscript() throws IOException {
        Path videoFile = tempDir.resolve("test.mp4");
        Files.write(videoFile, "fake video bytes".getBytes());

        when(callResponseSpec.content()).thenReturn("Video transcript here.");

        String result = service.transcribe(videoFile);

        assertThat(result).isEqualTo("Video transcript here.");
    }

    @Test
    void transcribe_unknownExtension_usesDefaultMime() throws IOException {
        Path file = tempDir.resolve("test.xyz");
        Files.write(file, "bytes".getBytes());

        when(callResponseSpec.content()).thenReturn("Some transcript.");

        String result = service.transcribe(file);

        assertThat(result).isEqualTo("Some transcript.");
    }
}
