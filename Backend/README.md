# Backend RAG Setup

This project includes a simple RAG (Retrieval-Augmented Generation) pipeline backed by Supabase (PostgreSQL + pgvector) and Google Gemini embeddings.

## What is included
- Document ingestion from `src/main/resources/documents` (PDF, DOCX, TXT).
- Chunking + embedding + storage in `kb_documents`.
- Vector search to build prompt context for `AiService`.
- Optional startup ingestion via config.

## Configuration
Set these environment variables (or equivalent Spring config):
- `GEMINI_API_KEY` - Google AI for Developers API key
- `DB_URL`, `DB_USER`, `DB_PASSWORD` - database connection

Key settings in `src/main/resources/application.yml`:
- `app.gemini.embedding-model` (default: `text-embedding-004`)
- `app.gemini.embedding-dimension` (default: `768`)
- `app.rag.top-k`, `app.rag.chunk-size`, `app.rag.max-context-chars`
- `app.rag.ingest-on-startup` (default: false)

## Ingestion
You can ingest documents manually:
- `POST /ai/ingest`

Or enable startup ingestion:
- Set `app.rag.ingest-on-startup=true`

## Quick start (local)
```bash
./gradlew bootRun
```

## Notes
- If you change the embedding model, update both `app.gemini.embedding-dimension` and the vector size in migration `V10__create_kb_documents.sql`.
- For large corpora, consider periodically re-indexing and tuning pgvector index parameters.

