package com.example.DocBackend.controller;

import com.example.DocBackend.dto.Dto;
import com.example.DocBackend.model.UploadedFile;
import com.example.DocBackend.service.AiService;
import com.example.DocBackend.service.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final AiService aiService;
    private final FileService fileService;

    @PostMapping
    public ResponseEntity<Dto.ChatResponse> chat(@Valid @RequestBody Dto.ChatRequest request) {
        UploadedFile file = fileService.findById(request.getFileId());

        String answer = aiService.answerQuestion(file.getExtractedText(), request.getQuestion());

        return ResponseEntity.ok(Dto.ChatResponse.builder()
                .fileId(request.getFileId())
                .question(request.getQuestion())
                .answer(answer)
                .build());
    }
}
