package com.nonsyncbobbal.enterprise_search_service.service;


import lombok.RequiredArgsConstructor;
import org.hibernate.loader.ast.spi.AfterLoadAction;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;

    public float[] generateEmbedding(String content) {
        {

            EmbeddingResponse response =
                    embeddingModel.embedForResponse(
                            List.of(content)
                    );

            return response
                    .getResults()
                    .getFirst()
                    .getOutput();
        }
    }
}
