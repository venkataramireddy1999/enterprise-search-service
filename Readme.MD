# Enterprise Search Service

A Spring Boot backend for semantic document search. Users register, log in with JWT, upload text documents, and search them by *meaning* rather than exact keyword match — powered by OpenAI embeddings stored and queried through `pgvector`.

## What it does

- **Auth** — user registration and login with BCrypt-hashed passwords, issuing a signed JWT on successful login.
- **Document upload** — authenticated users submit a title and text body; the content is persisted and simultaneously embedded and stored as a vector for search.
- **Semantic search** — a query string is embedded with the same OpenAI embedding model, then matched against stored document vectors using pgvector's distance operator, returning the closest results by meaning rather than literal keyword overlap.
- **Access control** — a document can only be fetched by the user who uploaded it or by an admin; everyone else gets a 403.
- **Centralized error handling** — validation errors, bad credentials, missing resources, and unexpected failures are all mapped to clean, consistent JSON responses instead of leaking stack traces.

## Tech stack

| Layer | Choice |
|---|---|
| Language / Runtime | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Security | Spring Security, stateless JWT auth (`jjwt`) |
| Persistence | Spring Data JPA, PostgreSQL |
| Vector search | `pgvector` extension, raw SQL via `JdbcTemplate` |
| Embeddings | Spring AI (`spring-ai-starter-model-openai`) |
| Local infra | Docker Compose (`pgvector/pgvector:pg16` image) |
| Build | Maven |

## How search actually works

1. On upload, `DocumentService` saves the document row, then calls `EmbeddingService` to generate a vector embedding of the content via the OpenAI embedding model, and inserts it into a separate `document_embeddings` table (`vector(1536)` column).
2. On search, the query text is embedded the same way, and `VectorSearchService` runs a nearest-neighbor query (`ORDER BY embedding <-> ?::vector LIMIT 5`) against that table.
3. Access control and the search index are deliberately decoupled: the vector table only exists to support ranking, and results returned by the current `/documents/search` endpoint are the top-5 matching content bodies (no per-document authorization filter is applied at this endpoint yet — see Roadmap).

## API endpoints

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/auth/register` | none | Register a new user |
| POST | `/auth/login` | none | Log in, returns a JWT |
| POST | `/documents/upload` | JWT (`USER`/`ADMIN`) | Upload a document; generates and stores its embedding |
| GET | `/documents/search?q=...` | JWT (`USER`/`ADMIN`) | Semantic search across stored documents |
| GET | `/documents/{id}` | JWT, owner or `ADMIN` | Fetch a single document |

## Running it locally

```bash
# 1. Start Postgres with the pgvector extension
docker compose up -d

# 2. Set the required environment variables
export POSTGRES_URL=jdbc:postgresql://localhost:5435/enterprise_search
export POSTGRES_USER=your_db_user
export POSTGRES_PASSWORD=your_db_password
export OPENAI_API_KEY=your_openai_key
export JWT_SECRET=a_long_random_secret

# 3. Run the app
./mvnw spring-boot:run
```

The app starts on port `8087`. Docker Compose maps the database to host port `5435`.

## Roadmap / known gaps

- Search currently returns matches across all documents rather than scoping to what the requesting user is allowed to see.
- No refresh-token flow — JWTs expire after 1 hour and require a fresh login.
- No pagination on search results (fixed top-5).
- Test coverage is currently limited to a Spring context load smoke test.

## Author

**Venkata Rami Reddy Bobbala** — Java Backend Developer
[LinkedIn](https://linkedin.com/in/venkataramireddy-bobbala) · [GitHub](https://github.com/venkataramireddy1999)