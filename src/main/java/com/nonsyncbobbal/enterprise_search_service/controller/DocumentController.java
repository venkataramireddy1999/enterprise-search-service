package com.nonsyncbobbal.enterprise_search_service.controller;

import com.nonsyncbobbal.enterprise_search_service.dto.DocumentUploadRequest;
import com.nonsyncbobbal.enterprise_search_service.entity.Document;
import com.nonsyncbobbal.enterprise_search_service.service.DocumentService;
import com.nonsyncbobbal.enterprise_search_service.repository.DocumentRepository;
import com.nonsyncbobbal.enterprise_search_service.service.VectorSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    private final VectorSearchService vectorSearchService;

    private final DocumentRepository documentRepository;

    @PostMapping("/upload")
    public ResponseEntity<Document> uploadDocument(
            @Valid @RequestBody DocumentUploadRequest request,
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
            @PathVariable Long id,
            Authentication authentication
    ) {

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Document not found: " + id
                ));

        boolean isOwner = document.getUploadedBy()
                .equals(authentication.getName());

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException(
                    "You do not have access to this document"
            );
        }

        return ResponseEntity.ok(document);
    }
}
