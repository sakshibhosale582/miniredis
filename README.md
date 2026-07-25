# Mini Redis

Mini Redis is a Spring Boot API with a React dashboard for managing an in-memory, LRU-backed key-value cache. The recommended deployment packages the React build into the Spring Boot application, so one Render web service serves both the dashboard and the API.

## Requirements

- Java 17
- Maven 3.9+
- Node.js 20.19+ or 22.12+
- Docker (optional, for the container workflow)

## Project layout

```text
mini-redis/
├── src/main/java/com/sakshi/miniredis/
│   ├── cache/             # Cache domain and persistence classes
│   ├── config/            # Typed cache and CORS configuration
│   ├── controller/        # REST endpoints
│   ├── dto/, exception/, response/
│   └── MiniRedisApplication.java
├── src/main/resources/application.properties
├── mini-redis-ui/         # React + Vite client
├── Dockerfile             # Builds the UI and API into one runtime image
└── render.yaml            # Render Blueprint
```

## Run locally

Start the backend from the repository root:

```bash
mvn spring-boot:run
```

The API starts at `http://localhost:8080`. Its health endpoint is `http://localhost:8080/actuator/health`, and Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

In a second terminal, start the React development server:

```bash
cd mini-redis-ui
npm install
npm run dev
```

Vite serves the dashboard at `http://localhost:5173` and proxies `/api` requests to `http://localhost:8080`. No browser CORS exception is needed during ordinary local development.

## API

All application endpoints are under `/api/cache`.

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/api/cache/health` | Application health response |
| `POST` | `/api/cache/set` | Store `{ "key": "...", "value": "...", "ttlSeconds": 60 }` (`ttlSeconds` is optional; omit it for a key that never expires) |
| `GET` | `/api/cache/get/{key}` | Get a value |
| `DELETE` | `/api/cache/delete/{key}` | Delete a key |
| `GET` | `/api/cache/exists/{key}` | Check for a key |
| `GET` | `/api/cache/keys` | List keys |
| `GET` | `/api/cache/stats` | Cache statistics |
| `GET` | `/api/cache/history` | Recent commands |
| `POST` | `/api/cache/save` | Persist the active cache to disk |
| `POST` | `/api/cache/load` | Restore the cache from disk |
| `POST` | `/api/cache/clear` | Clear the cache |

Successful responses use `{ "success", "message", "data" }`; failures consistently include a status, message, request path, and timestamp.

## Configuration

Backend variables are read at runtime. Values in parentheses are defaults.

| Variable | Purpose |
| --- | --- |
| `PORT` (`8080`) | HTTP port; Render supplies this automatically. |
| `CACHE_MAX_SIZE` (`1000`) | Maximum LRU cache entries. |
| `CACHE_HISTORY_SIZE` (`20`) | Number of commands retained in history. |
| `CACHE_PERSISTENCE_FILE` (`./data/mini-redis-cache.dat`) | Location for saved cache data. |
| `CORS_ALLOWED_ORIGINS` (`http://localhost:5173`) | Comma-separated origins allowed to call `/api/**`. |

The React build reads its public configuration at build time. Copy [`mini-redis-ui/.env.example`](mini-redis-ui/.env.example) to `.env.local` only when an override is needed.

| Variable | Purpose |
| --- | --- |
| `VITE_API_BASE_URL` (`/api/cache`) | API base URL used by Axios. The relative default works in Docker and through Vite's local proxy. |
| `VITE_DEV_API_PROXY_TARGET` (`http://localhost:8080`) | Backend URL used only by the Vite development proxy. |

Never put secrets in a `VITE_*` variable: Vite exposes them to the browser bundle.

## Docker

The multi-stage image installs locked frontend dependencies, creates a production Vite bundle, copies that bundle into Spring Boot static resources, and runs the resulting JAR as a non-root user.

```bash
docker build -t mini-redis .
docker run --rm -p 8080:8080 -v mini-redis-data:/app/data mini-redis
```

The volume is optional but recommended when running Docker locally because it preserves data written by the save endpoint across container replacement.

## Deploy on Render

1. Push this repository to GitHub, GitLab, or Bitbucket.
2. In Render, select **New → Blueprint** and select the repository. Render reads [`render.yaml`](render.yaml), builds the root `Dockerfile`, and probes `/actuator/health`.
3. Deploy. The dashboard and API are served from the same Render URL, so the default relative `VITE_API_BASE_URL=/api/cache` works without production CORS configuration.

For a separately hosted React static site, set `VITE_API_BASE_URL` to `https://<your-api>.onrender.com/api/cache` **before building it**, and set the API service's `CORS_ALLOWED_ORIGINS` to that exact static-site origin. Comma-separate additional allowed origins if necessary.

This is an in-memory cache. Render's default filesystem is ephemeral, so data saved with `/save` is not durable across restarts or redeploys. Use a persistent disk on a paid service or an external datastore if durable production cache data is required.

## Verification

```bash
mvn clean test
cd mini-redis-ui && npm run lint && npm run build
```
