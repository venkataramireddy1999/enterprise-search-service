package com.nonsyncbobbal.enterprise_search_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;

    public float[] generateEmbedding(String content) {

        EmbeddingResponse response =
                embeddingModel.embedForResponse(
                        List.of(content)
                );

        return response
                .getResults()
                .getFirst()
                .getOutput();
    }

    /**
     * Converts a raw embedding array into the pgvector text literal format,
     * e.g. "[0.123,0.456,...]". float[].toString() must NEVER be used for this
     * purpose - it returns the array's identity hash (e.g. "[F@1b6d3586"), not
     * its contents, which would silently corrupt every stored embedding.
     */
    public String toVectorLiteral(float[] embedding) {

        return IntStream.range(0, embedding.length)
                .mapToObj(i -> String.valueOf(embedding[i]))
                .collect(Collectors.joining(",", "[", "]"));
    }
}
