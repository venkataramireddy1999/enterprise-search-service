package com.nonsyncbobbal.enterprise_search_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VectorSearchService {

    private final JdbcTemplate jdbcTemplate;

    private final EmbeddingService embeddingService;

    public List<String> semanticSearch(
            String query
    ) {

        float[] embedding =
                embeddingService.generateEmbedding(query);

        String sql = """
            SELECT content
            FROM document_embeddings
            ORDER BY embedding <-> ?::vector
            LIMIT 5
            """;

        return jdbcTemplate.queryForList(
                sql,
                String.class,
                embedding.toString()
        );
    }
}