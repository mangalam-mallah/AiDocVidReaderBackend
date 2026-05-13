package com.example.DocBackend.serviceTest;

import com.example.DocBackend.dto.Dto;
import com.example.DocBackend.model.UploadedFile;
import com.example.DocBackend.repository.UploadedFileRepository;
import com.example.DocBackend.service.FileService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @Mock private UploadedFileRepository fileRepository;
    @Mock private FileStorageServiceTest storageService;

    @InjectMocks private FileService fileService;

    private UploadedFile sampleFile(Long id, String type) {
        return UploadedFile.builder()
                .id(id).originalName("test." + type).storedName("uuid." + type)
                .fileType(type).filePath("/uploads/uuid." + type)
                .extractedText("Some text").fileSizeBytes(1024L)
                .uploadedAt(LocalDateTime.now()).build();
    }

    @Test
    void uploadFile_unsupportedType_throwsException() {
        MockMultipartFile file = new MockMultipartFile("file", "archive.zip",
                "application/zip", "data".getBytes());
        when(storageService.detectFileType("archive.zip")).thenReturn("unknown");

        assertThatThrownBy(() -> fileService.uploadFile(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported file type");
    }

    @Test
    void getFileInfo_returnsCorrectDto() {
        when(fileRepository.findById(1L)).thenReturn(Optional.of(sampleFile(1L, "pdf")));

        Dto.FileInfoResponse info = fileService.getFileInfo(1L);

        assertThat(info.getId()).isEqualTo(1L);
        assertThat(info.getExtractedText()).isEqualTo("Some text");
    }

    @Test
    void findById_notFound_throwsEntityNotFoundException() {
        when(fileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fileService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void listFiles_returnsAllFiles() {
        when(fileRepository.findAll()).thenReturn(List.of(
                sampleFile(1L, "pdf"), sampleFile(2L, "audio")));

        List<Dto.FileInfoResponse> list = fileService.listFiles();

        assertThat(list).hasSize(2);
    }

    @Test
    void saveSummary_updatesSummaryField() {
        UploadedFile file = sampleFile(1L, "pdf");
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(fileRepository.save(any())).thenReturn(file);

        fileService.saveSummary(1L, "A summary.");

        verify(fileRepository).save(argThat(f -> "A summary.".equals(f.getSummary())));
    }

}
