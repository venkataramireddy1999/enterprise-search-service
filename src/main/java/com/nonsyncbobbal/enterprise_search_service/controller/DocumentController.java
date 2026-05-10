package com.nonsyncbobbal.enterprise_search_service.controller;

import com.nonsyncbobbal.enterprise_search_service.dto.DocumentUploadRequest;
import com.nonsyncbobbal.enterprise_search_service.entity.Document;
import com.nonsyncbobbal.enterprise_search_service.service.DocumentService;
import com.nonsyncbobbal.enterprise_search_service.repository.DocumentRepository;
import com.nonsyncbobbal.enterprise_search_service.service.VectorSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    private final VectorSearchService vectorSearchService;

    private final DocumentRepository documentRepository;

    @PostMapping("/upload")
    public ResponseEntity<Document> uploadDocument(
            @RequestBody DocumentUploadRequest request,
            Authentication authentication
    ) {

        Document document =
                documentService.uploadDocument(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(document);
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> search(
            @RequestParam String q
    ) {

        return ResponseEntity.ok(
                vectorSearchService.semanticSearch(q)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocument(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                documentRepository.findById(id)
                        .orElseThrow()
        );
    }
}
