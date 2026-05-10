package com.nonsyncbobbal.enterprise_search_service.service;

import com.nonsyncbobbal.enterprise_search_service.dto.DocumentUploadRequest;
import com.nonsyncbobbal.enterprise_search_service.entity.Document;
import com.nonsyncbobbal.enterprise_search_service.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    private final EmbeddingService embeddingService;

    private final JdbcTemplate jdbcTemplate;

    public Document uploadDocument(DocumentUploadRequest request, String uploadedBy) {

        Document document = Document.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .uploadedBy(uploadedBy)
                .uploadedAt(LocalDateTime.now())
                .build();

        Document saved =
                documentRepository.save(document);

        float[] embedding =
                embeddingService.generateEmbedding(
                        request.getContent()
                );

        String sql = """
                INSERT INTO document_embeddings
                (document_id, content, embedding)
                VALUES (?, ?, ?::vector)
                """;

        jdbcTemplate.update(
                sql,
                saved.getId(),
                request.getContent(),
                embedding.toString()
        );

        return saved;
    }
}
