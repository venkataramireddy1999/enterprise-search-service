CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS document_embeddings (
          id BIGSERIAL PRIMARY KEY,
          document_id BIGINT,
          content TEXT,
          embedding vector(1536)
    );