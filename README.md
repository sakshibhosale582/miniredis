Mini Redis

Mini Redis is a Redis-inspired full-stack in-memory key-value cache management system built with Java 17, Spring Boot, React, and Vite. It provides a browser dashboard and REST API for managing cache entries with LRU eviction, optional TTL expiration, cache statistics, command history, persistence, Docker deployment, Swagger/OpenAPI, and health monitoring.

Mini Redis is an educational Redis-inspired implementation. It demonstrates core caching concepts and is not intended to replace the official Redis server.

Live Project

Live Dashboard: https://miniredis-274e.onrender.com/

GitHub Repository: https://github.com/sakshibhosale582/miniredis

Application Preview

Dashboard Overview

The dashboard provides a single interface for cache operations and displays backend connection status.



Cache Operations and System Statistics

Users can store, retrieve, delete, clear, save, and load cache entries. TTL is optional. The dashboard displays active keys, cache hits, cache misses, and hit ratio.



Current Keys and Command History

The interface displays current cache keys and a recent history of operations such as SET, GET, DELETE, CLEAR, SAVE, and LOAD.



Features

In-memory key-value cache

LRU (Least Recently Used) cache management

Optional TTL (Time To Live) expiration

Store, retrieve, delete, check, list, and clear keys

Manual save and load persistence operations

Cache hits, misses, active keys, and hit-ratio statistics

Recent command history

RESTful Spring Boot API

React + Vite dashboard

Consistent success and error responses

Typed cache and CORS configuration

Swagger/OpenAPI documentation

Spring Boot Actuator health endpoint

Docker multi-stage production build

Render Blueprint deployment

Maven tests and frontend lint/build verification

How It Works

Mini Redis stores key-value data in application memory for fast access. Entries may optionally have a TTL, after which they are treated as expired. The cache is LRU-backed, allowing older, less recently used entries to be evicted when the configured maximum size is reached.

React Dashboard / API Client
            |
            v
      CacheController
            |
            v
       CacheManager
            |
      +-----+------+
      |            |
      v            v
   LRU Cache   Cache Statistics
      |
      +----> Persistence / Command History

In the recommended production deployment, the React application is built into Spring Boot static resources so a single web service serves both the dashboard and REST API.

Technology Stack

Layer

Technology

Backend

Java 17, Spring Boot

Frontend

React, Vite

Communication

REST API / JSON

Cache

In-memory LRU-backed key-value cache

Build Tools

Maven, npm

API Documentation

Swagger / OpenAPI

Monitoring

Spring Boot Actuator

Containerization

Docker

Deployment

Render

Verification

Maven tests, frontend lint and build

Project Structure

mini-redis/
├── src/main/java/com/sakshi/miniredis/
│   ├── cache/             # Cache domain, LRU and persistence classes
│   ├── config/            # Typed cache and CORS configuration
│   ├── controller/        # REST endpoints
│   ├── dto/               # Data transfer objects
│   ├── exception/         # Exception handling
│   ├── response/          # Standard API responses
│   └── MiniRedisApplication.java
├── src/main/resources/
│   └── application.properties
├── mini-redis-ui/         # React + Vite client
├── docs/screenshots/      # README screenshots
├── Dockerfile             # Builds UI and API into one runtime image
├── render.yaml            # Render Blueprint
├── pom.xml
└── README.md

Requirements

Java 17

Maven 3.9+

Node.js 20.19+ or 22.12+

npm

Docker (optional)

Check installations:

java -version
mvn -version
node -v
npm -v

Setup Instructions

Backend

From the repository root:

mvn spring-boot:run

Backend:

http://localhost:8080

Health endpoint:

http://localhost:8080/actuator/health

Swagger UI:

http://localhost:8080/swagger-ui.html

Frontend

Open another terminal:

cd mini-redis-ui
npm install
npm run dev

Dashboard:

http://localhost:5173

Vite proxies /api requests to http://localhost:8080, so no browser CORS exception is required during the standard local-development workflow.

API Reference

All application endpoints are under /api/cache.

Method

Endpoint

Purpose

GET

/api/cache/health

Application health response

POST

/api/cache/set

Store a key-value pair with optional TTL

GET

/api/cache/get/{key}

Retrieve a value

DELETE

/api/cache/delete/{key}

Delete a key

GET

/api/cache/exists/{key}

Check whether a key exists

GET

/api/cache/keys

List active keys

GET

/api/cache/stats

Cache statistics

GET

/api/cache/history

Recent commands

POST

/api/cache/save

Persist active cache data to disk

POST

/api/cache/load

Restore cache data from disk

POST

/api/cache/clear

Clear the cache

Store an Entry

POST /api/cache/set
Content-Type: application/json

{
  "key": "name",
  "value": "Sakshi",
  "ttlSeconds": 60
}

ttlSeconds is optional. Omit it for a key that should not expire based on TTL.

Retrieve an Entry

GET /api/cache/get/name

Delete an Entry

DELETE /api/cache/delete/name

API Response Format

Successful responses use:

{
  "success": true,
  "message": "Operation completed successfully",
  "data": {}
}

Failures consistently include a status, message, request path, and timestamp.

Configuration

Backend values are read at runtime.

Variable

Default

Purpose

PORT

8080

HTTP port; Render supplies this automatically

CACHE_MAX_SIZE

1000

Maximum LRU cache entries

CACHE_HISTORY_SIZE

20

Number of commands retained in history

CACHE_PERSISTENCE_FILE

./data/mini-redis-cache.dat

Location for saved cache data

CORS_ALLOWED_ORIGINS

http://localhost:5173

Comma-separated origins allowed to call /api/**

Frontend Configuration

Copy mini-redis-ui/.env.example to .env.local only when an override is required.

Variable

Default

Purpose

VITE_API_BASE_URL

/api/cache

API base URL used by Axios

VITE_DEV_API_PROXY_TARGET

http://localhost:8080

Backend URL used by the Vite development proxy

Security: Never place secrets in a VITE_* variable. Vite exposes these values to the browser bundle.

Building for Production

Frontend:

cd mini-redis-ui
npm install
npm run build

Backend:

mvn clean package

Run the generated Spring Boot JAR:

java -jar target/*.jar

The recommended production workflow uses the root Dockerfile to build the frontend and backend together.

Docker

The multi-stage Docker image installs locked frontend dependencies, creates a production Vite bundle, copies that bundle into Spring Boot static resources, builds the JAR, and runs the resulting application as a non-root user.

docker build -t mini-redis .
docker run --rm -p 8080:8080 -v mini-redis-data:/app/data mini-redis

Open http://localhost:8080.

The volume is optional but recommended for local Docker use because it preserves data written by the save endpoint across container replacement.

Deployment

Mini Redis is deployed on Render:

https://miniredis-274e.onrender.com/

The production setup serves the React dashboard and Spring Boot API from the same Render web service.

Browser
   |
   v
Render Web Service
   |
   +---- React Dashboard
   |
   +---- Spring Boot REST API
              |
              v
        In-Memory Cache

Deploy with Render Blueprint

Push the repository to GitHub, GitLab, or Bitbucket.

In Render, select New → Blueprint.

Select the repository.

Render reads render.yaml.

The root Dockerfile builds the UI and API.

Render probes /actuator/health.

Deploy the service.

Because the dashboard and API share one origin, the relative VITE_API_BASE_URL=/api/cache works without separate production CORS configuration.

Separately Hosted React Frontend

Set before building:

VITE_API_BASE_URL=https://<your-api>.onrender.com/api/cache

Configure the backend with the exact frontend origin:

CORS_ALLOWED_ORIGINS=https://<your-frontend-domain>

Comma-separate additional allowed origins when required.

Persistence Note

Mini Redis is primarily an in-memory cache.

The /save and /load operations provide file-based persistence, but Render's default filesystem is ephemeral. Saved files should therefore not be considered durable across restarts or redeployments.

Use a persistent disk on a suitable service or an external datastore when durable production cache data is required.

Verification

Backend:

mvn clean test

Frontend:

cd mini-redis-ui
npm run lint
npm run build

Key Concepts Demonstrated

Key-value caching

In-memory data management

LRU cache eviction

TTL-based expiration

Cache hits and misses

Hit-ratio monitoring

REST API design

Spring Boot backend architecture

React-to-backend communication

File-based persistence

Centralized API error handling

Runtime configuration

Dockerized full-stack deployment

Health monitoring and API documentation

Limitations

Mini Redis is an educational Redis-inspired implementation and does not provide the complete functionality of production Redis. It does not aim to implement features such as distributed clustering, replication, high availability, Pub/Sub, the Redis wire protocol, or the full range of Redis data structures.

Its purpose is to demonstrate the core ideas behind in-memory caching and cache management through a full-stack application.

Future Enhancements

Distributed cache support

Authentication and authorization

Additional cache eviction strategies

More automated testing

Advanced monitoring and metrics

Durable external persistence

Rate limiting

CI/CD automation

Additional Redis-like data structures

Enhanced cache visualization and analytics

Contributing

Fork the repository.

Create a feature branch:

git checkout -b feature-name

Make and commit your changes:

git commit -m "Add feature description"

Push the branch:

git push origin feature-name

Open a Pull Request.

Author

Sakshi Bhosale

GitHub: https://github.com/sakshibhosale582

Repository: https://github.com/sakshibhosale582/miniredis

Live Project: https://miniredis-274e.onrender.com/

License

This project is licensed under the MIT License.

See the LICENSE file in the repository for the complete license text.

Mini Redis

A practical Redis-inspired cache management system built with Spring Boot and React to explore in-memory caching, LRU eviction, TTL expiration, persistence, REST APIs, monitoring, and full-stack deployment.
