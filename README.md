# AI-Powered Document & Multimedia Q&A — Backend

Spring Boot 3 + Spring AI backend for uploading PDFs, audio, and video files and asking AI-powered questions about them.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.3, Spring AI 1.0 |
| AI | Google Gemini 1.5 Pro (chat + transcription via multimodal) |
| PDF Extraction | Apache PDFBox 3 |
| Database | PostgreSQL |
| Containerization | Docker + Docker Compose |
| Test Coverage |

---

## Quick Start

### Prerequisites
- Java 21+
- Docker & Docker Compose
- Gemini API Key

### 1. Clone and configure

```bash
git clone <repo-url>
cd qa-backend
cp .env.example .env
# Set GEMINI_API_KEY in .env  →  get it free at https://aistudio.google.com/apikey
```

### 2. Run with Docker Compose

```bash
docker compose up --build
```

Backend runs at **http://localhost:8080**

### 3. Run locally (without Docker)

```bash
# Start PostgreSQL
docker run -d -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=qaapp -p 5432:5432 postgres:16-alpine

# Run the app
export GEMINI_API_KEY=your-key-here
mvn spring-boot:run
```

---

## API Reference

### Upload a File
```
POST /api/files/upload
Content-Type: multipart/form-data

file: <PDF, MP3, MP4, WAV, MOV, etc.>
```
**Response:**
```json
{
  "id": 1,
  "originalName": "report.pdf",
  "fileType": "pdf",
  "fileSizeBytes": 204800,
  "uploadedAt": "2024-01-15T10:30:00",
  "message": "File uploaded and text extracted successfully."
}
```

---

### Get File Info
```
GET /api/files/{id}
```

---

### List All Files
```
GET /api/files
```

---

### Ask a Question
```
POST /api/chat
Content-Type: application/json

{
  "fileId": 1,
  "question": "What are the main conclusions?"
}
```
**Response:**
```json
{
  "fileId": 1,
  "question": "What are the main conclusions?",
  "answer": "Based on the document, the main conclusions are..."
}
```

---

### Summarize a File
```
POST /api/summarize/{fileId}
```
**Response:**
```json
{
  "fileId": 1,
  "summary": "This document covers..."
}
```

---

### Get Timestamps (Audio/Video only)
```
GET /api/timestamps/{fileId}
```
**Response:**
```json
{
  "fileId": 2,
  "timestamps": [
    { "topic": "Introduction", "timestamp": "00:00:00", "description": "Speaker introduces the topic." },
    { "topic": "Key Findings", "timestamp": "00:05:30", "description": "Main research findings presented." }
  ]
}
```

---

## Running Tests

```bash
mvn test
```

Coverage report: `target/site/jacoco/index.html`

```bash
mvn verify
```

---

## Environment Variables

| Variable | Description | Default |
|---|---|---|
| `GEMINI_API_KEY` | Your Google Gemini API key | *(required)* |
| `DB_URL` | JDBC URL | `jdbc:postgresql://localhost:5432/qaapp` |
| `DB_USER` | DB username | `postgres` |
| `DB_PASSWORD` | DB password | `postgres` |
| `UPLOAD_DIR` | File upload directory | `./uploads` |

---

