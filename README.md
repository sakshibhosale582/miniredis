<div align="center">

# ⚡ Mini Redis

### 🧠 Redis-Inspired In-Memory Cache Management System

**Spring Boot • React • LRU Cache • TTL • REST API • Docker • Render**

[![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk&logoColor=white)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Backend-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-Frontend-61DAFB?logo=react&logoColor=black)](https://react.dev/)
[![Vite](https://img.shields.io/badge/Vite-Build-646CFF?logo=vite&logoColor=white)](https://vite.dev/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![Render](https://img.shields.io/badge/Render-Deployed-46E3B7?logo=render&logoColor=black)](https://render.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

<br>

[🚀 **Live Demo**](https://miniredis-274e.onrender.com/) •
[💻 **Source Code**](https://github.com/sakshibhosale582/miniredis) •
[📖 **API Docs**](https://miniredis-274e.onrender.com/swagger-ui.html)

</div>

---

## 📌 About the Project

**Mini Redis** is a Redis-inspired, full-stack **in-memory key-value cache management system** built using **Java 17, Spring Boot, React, and Vite**.

The project demonstrates how a caching system works internally instead of simply using Redis as an external service.

It provides a browser-based dashboard and REST API for managing cache entries and includes important caching concepts such as:

- ⚡ In-memory key-value storage
- 🔄 LRU cache management
- ⏳ TTL-based expiration
- 📊 Cache statistics
- 🕘 Command history
- 💾 File-based persistence
- 🔌 REST APIs
- 🐳 Docker deployment
- ☁️ Render deployment
- 📖 Swagger/OpenAPI documentation
- ❤️ Spring Boot Actuator health monitoring

> 💡 **Note:** Mini Redis is an educational Redis-inspired implementation designed to demonstrate core caching concepts. It is not intended to replace the official Redis server.

---

## 🔗 Quick Links

| Resource | Link |
| --- | --- |
| 🚀 Live Application | [Open Mini Redis](https://miniredis-274e.onrender.com/) |
| 💻 GitHub Repository | [View Source Code](https://github.com/sakshibhosale582/miniredis) |
| ❤️ Health Check | [Actuator Health](https://miniredis-274e.onrender.com/actuator/health) |
| 📖 Swagger UI | [API Documentation](https://miniredis-274e.onrender.com/swagger-ui.html) |

---

# 🖥️ Application Preview

## 🏠 Dashboard Overview

The Mini Redis dashboard provides a simple interface for interacting with the cache.

From the dashboard, users can:

- Store new cache entries
- Retrieve values
- Delete keys
- Clear the complete cache
- Save cache data
- Load persisted data
- View backend connection status

![Mini Redis Dashboard](docs/screenshots/dashboard-overview.png)

---

## 📊 Cache Operations & System Statistics

The dashboard allows users to perform cache operations using a **key, value, and optional TTL**.

It also provides real-time cache information such as:

- Active keys
- Cache hits
- Cache misses
- Hit ratio
- Backend connection status

![Cache Operations and Statistics](docs/screenshots/cache-statistics.png)

---

## 🗂️ Current Keys & Command History

Mini Redis displays the currently active cache keys and keeps track of recent operations.

The command history can include operations such as:

```text
SET
GET
DELETE
CLEAR
SAVE
LOAD
```

This makes it easier to understand and monitor cache activity.

![Current Keys and Command History](docs/screenshots/keys-command-history.png)

---

# ✨ Features

- ⚡ **In-memory key-value cache**
- 🔄 **LRU (Least Recently Used) cache management**
- ⏳ **Optional TTL (Time To Live) expiration**
- ➕ Store cache entries
- 🔎 Retrieve cache values
- 🗑️ Delete cache entries
- ✅ Check whether a key exists
- 🗂️ List active cache keys
- 🧹 Clear the cache
- 💾 Manual **save and load** persistence
- 📈 Cache **hits, misses, active keys, and hit ratio**
- 🕘 Recent **command history**
- 🔌 RESTful Spring Boot API
- 🖥️ React + Vite dashboard
- 📦 Consistent API response structure
- 🛡️ Centralized exception handling
- 🔧 Runtime cache configuration
- 🌐 CORS configuration
- 📚 Swagger/OpenAPI documentation
- ❤️ Spring Boot Actuator health endpoint
- 🐳 Multi-stage Docker production build
- ☁️ Render Blueprint deployment
- 🧪 Backend tests and frontend verification

---

# ⚙️ How Mini Redis Works

Mini Redis stores key-value data primarily in **application memory**.

Because the data is kept in memory, cache operations can be performed quickly without repeatedly accessing persistent storage.

A simplified request flow looks like this:

```text
            React Dashboard
                   |
                   |
                   v
             REST API Request
                   |
                   v
          +------------------+
          | CacheController  |
          +------------------+
                   |
                   v
          +------------------+
          |   CacheManager   |
          |  Business Logic  |
          +------------------+
                   |
          +--------+---------+
          |                  |
          v                  v
      LRU Cache          Cache Stats
          |
          +------------------+
          |                  |
          v                  v
  Command History     Persistence
```

The **React dashboard** communicates with the **Spring Boot REST API**.

The Controller receives the HTTP request and forwards the required operation to the cache management layer.

The cache system then manages:

- Key-value storage
- LRU ordering
- TTL expiration
- Cache statistics
- Persistence
- Command history

---

# 🔄 LRU Cache Management

Mini Redis uses an **LRU — Least Recently Used** cache strategy.

Suppose the maximum cache capacity is three entries:

```text
A → Apple
B → Ball
C → Cat
```

If `A` is accessed again, it becomes recently used.

Conceptually:

```text
B → C → A
↑       ↑
LRU     Most Recently Used
```

Now suppose a new entry is added:

```text
D → Dog
```

If the cache has reached its configured maximum capacity, the least recently used entry can be removed.

```text
B ❌ Removed

C
A
D
```

This helps keep frequently/recently accessed data available while controlling cache size.

---

# ⏳ TTL — Time To Live

Cache entries can optionally contain a **TTL value**.

TTL defines how long an entry should remain valid.

Example:

```text
Key   = otp
Value = 458921
TTL   = 60 seconds
```

During the TTL period:

```text
GET otp
→ 458921
```

After expiration, the entry is no longer treated as valid.

TTL is useful for temporary information such as:

- OTPs
- Sessions
- Temporary tokens
- Frequently changing API responses
- Short-lived cached data

If `ttlSeconds` is omitted when creating an entry, the key does not expire based on TTL.

---

# 📊 Cache Statistics

Mini Redis tracks cache performance information.

Important statistics include:

### Cache Hit

A **cache hit** occurs when the requested key exists and its value can be returned.

```text
GET name

name → Sakshi

Result: HIT ✅
```

### Cache Miss

A **cache miss** occurs when the requested key is not available.

```text
GET salary

salary → Not Found

Result: MISS ❌
```

### Hit Ratio

The hit ratio helps measure cache effectiveness.

```text
Hit Ratio = Cache Hits / Total Cache Lookups
```

For example:

```text
Hits   = 80
Misses = 20

Total = 100
Hit Ratio = 80%
```

---

# 💾 Persistence

Mini Redis primarily stores data in memory.

The project also provides manual persistence operations:

```text
SAVE
LOAD
```

The save operation writes active cache data to a configured persistence file.

The load operation restores saved cache information.

Conceptually:

```text
        CACHE
          |
          v
 PersistenceManager
          |
          v
         FILE
```

During restoration:

```text
         FILE
          |
          v
 PersistenceManager
          |
          v
        CACHE
```

---

# 🧰 Technology Stack

| Layer | Technology |
| --- | --- |
| ☕ Backend | Java 17 |
| 🌱 Framework | Spring Boot |
| ⚛️ Frontend | React |
| ⚡ Frontend Build Tool | Vite |
| 🔌 Communication | REST API / JSON |
| 🧠 Cache | In-memory LRU-backed key-value cache |
| 📦 Backend Build | Maven |
| 📦 Frontend Packages | npm |
| 📖 API Documentation | Swagger / OpenAPI |
| ❤️ Monitoring | Spring Boot Actuator |
| 🐳 Containerization | Docker |
| ☁️ Deployment | Render |
| 🧪 Verification | Maven Tests + Frontend Lint/Build |

---

# 📁 Project Structure

```text
mini-redis/
│
├── src/
│   └── main/
│       │
│       ├── java/com/sakshi/miniredis/
│       │   │
│       │   ├── cache/
│       │   │   └── Cache domain, LRU and persistence logic
│       │   │
│       │   ├── config/
│       │   │   └── Cache and CORS configuration
│       │   │
│       │   ├── controller/
│       │   │   └── REST API endpoints
│       │   │
│       │   ├── dto/
│       │   │   └── Data Transfer Objects
│       │   │
│       │   ├── exception/
│       │   │   └── Exception handling
│       │   │
│       │   ├── response/
│       │   │   └── Standard API responses
│       │   │
│       │   └── MiniRedisApplication.java
│       │
│       └── resources/
│           └── application.properties
│
├── mini-redis-ui/
│   └── React + Vite frontend
│
├── docs/
│   └── screenshots/
│
├── Dockerfile
├── render.yaml
├── pom.xml
└── README.md
```

---

# ✅ Requirements

Before running Mini Redis locally, install:

- ☕ **Java 17**
- 📦 **Maven 3.9+**
- 🟢 **Node.js 20.19+ or 22.12+**
- 📦 **npm**
- 🐳 **Docker** *(optional)*

Check installed versions:

```bash
java -version
mvn -version
node -v
npm -v
```

---

# 🚀 Setup Instructions

## ☕ 1. Start the Spring Boot Backend

Clone the repository:

```bash
git clone https://github.com/sakshibhosale582/miniredis.git
```

Move into the project:

```bash
cd miniredis
```

Start Spring Boot:

```bash
mvn spring-boot:run
```

The API starts at:

```text
http://localhost:8080
```

---

## ❤️ Backend Health Check

Open:

```text
http://localhost:8080/actuator/health
```

---

## 📖 Swagger API Documentation

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

Swagger provides an interactive interface for viewing and testing the REST endpoints.

---

## ⚛️ 2. Start the React Frontend

Open another terminal:

```bash
cd mini-redis-ui
```

Install dependencies:

```bash
npm install
```

Start the Vite development server:

```bash
npm run dev
```

The dashboard will run at:

```text
http://localhost:5173
```

During normal local development, Vite proxies `/api` requests to:

```text
http://localhost:8080
```

Therefore, a browser CORS exception is not required for the standard local-development workflow.

---

# 🔌 API Reference

All application endpoints are available under:

```text
/api/cache
```

| Method | Endpoint | Description |
| --- | --- | --- |
| 🟢 `GET` | `/api/cache/health` | Check application health |
| 🔵 `POST` | `/api/cache/set` | Store a key-value pair |
| 🟢 `GET` | `/api/cache/get/{key}` | Retrieve a cache value |
| 🔴 `DELETE` | `/api/cache/delete/{key}` | Delete a cache entry |
| 🟢 `GET` | `/api/cache/exists/{key}` | Check whether a key exists |
| 🟢 `GET` | `/api/cache/keys` | List active cache keys |
| 🟢 `GET` | `/api/cache/stats` | View cache statistics |
| 🟢 `GET` | `/api/cache/history` | View command history |
| 🔵 `POST` | `/api/cache/save` | Save cache data |
| 🔵 `POST` | `/api/cache/load` | Restore saved cache data |
| 🔵 `POST` | `/api/cache/clear` | Clear the cache |

---

# ➕ Store an Entry

### Request

```http
POST /api/cache/set
Content-Type: application/json
```

### Body

```json
{
  "key": "name",
  "value": "Sakshi",
  "ttlSeconds": 60
}
```

`ttlSeconds` is optional.

For a key that should not expire based on TTL:

```json
{
  "key": "name",
  "value": "Sakshi"
}
```

---

# 🔎 Retrieve an Entry

```http
GET /api/cache/get/name
```

---

# 🗑️ Delete an Entry

```http
DELETE /api/cache/delete/name
```

---

# 📦 API Response Format

Successful API responses follow a consistent structure:

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {}
}
```

Failure responses consistently include information such as:

```json
{
  "status": 404,
  "message": "Requested resource was not found",
  "path": "/api/cache/get/example",
  "timestamp": "..."
}
```

This makes API responses predictable for both the React frontend and other API clients.

---

# 🔧 Backend Configuration

Backend configuration is read at runtime.

| Environment Variable | Default | Purpose |
| --- | --- | --- |
| `PORT` | `8080` | HTTP server port |
| `CACHE_MAX_SIZE` | `1000` | Maximum number of LRU cache entries |
| `CACHE_HISTORY_SIZE` | `20` | Number of recent commands retained |
| `CACHE_PERSISTENCE_FILE` | `./data/mini-redis-cache.dat` | Persistence file location |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Origins allowed to access `/api/**` |

Render automatically provides the `PORT` value during deployment.

---

# 🌐 Frontend Configuration

The React build reads public configuration at **build time**.

Copy:

```text
mini-redis-ui/.env.example
```

to:

```text
mini-redis-ui/.env.local
```

only when an override is required.

| Variable | Default | Purpose |
| --- | --- | --- |
| `VITE_API_BASE_URL` | `/api/cache` | API base URL used by Axios |
| `VITE_DEV_API_PROXY_TARGET` | `http://localhost:8080` | Backend target used by Vite |

> 🔐 **Security Warning:** Never place passwords, tokens, API keys, or other secrets inside a `VITE_*` environment variable because Vite exposes these values to the browser bundle.

---

# 🏗️ Production Build

## ⚛️ Build the Frontend

```bash
cd mini-redis-ui
npm install
npm run build
```

---

## ☕ Build the Backend

From the repository root:

```bash
mvn clean package
```

Run the generated Spring Boot JAR:

```bash
java -jar target/*.jar
```

The recommended production workflow uses the project's root `Dockerfile` to build the frontend and backend together.

---

# 🐳 Docker

Mini Redis includes a **multi-stage Docker build**.

The production Docker workflow:

```text
Install frontend dependencies
          ↓
Build React/Vite application
          ↓
Copy frontend build into Spring Boot
          ↓
Build Spring Boot JAR
          ↓
Create runtime image
          ↓
Run application as non-root user
```

Build the Docker image:

```bash
docker build -t mini-redis .
```

Run the container:

```bash
docker run --rm -p 8080:8080 -v mini-redis-data:/app/data mini-redis
```

Open:

```text
http://localhost:8080
```

The Docker volume is optional but recommended when running locally because it preserves data written through the save operation across container replacement.

---

# ☁️ Deployment

Mini Redis is deployed on **Render**.

## 🚀 Live Application

### https://miniredis-274e.onrender.com/

The recommended production architecture serves the compiled React dashboard and Spring Boot API from the **same Render web service**.

```text
                  Browser
                     |
                     v
             Render Web Service
                     |
             +-------+-------+
             |               |
             v               v
      React Dashboard   Spring Boot API
                             |
                             v
                      In-Memory Cache
```

---

# 🚀 Deploy Using Render Blueprint

### Step 1

Push the repository to:

- GitHub
- GitLab
- Bitbucket

### Step 2

Sign in to Render.

### Step 3

Select:

```text
New → Blueprint
```

### Step 4

Select the Mini Redis repository.

### Step 5

Render reads:

```text
render.yaml
```

### Step 6

The root:

```text
Dockerfile
```

builds the React frontend and Spring Boot backend.

### Step 7

Render checks:

```text
/actuator/health
```

### Step 8

Deploy the application.

Because both the dashboard and API are served from the same origin:

```text
VITE_API_BASE_URL=/api/cache
```

works without separate production CORS configuration.

---

# 🌍 Separately Hosted React Frontend

If the React application is hosted separately, configure:

```text
VITE_API_BASE_URL=https://<your-api>.onrender.com/api/cache
```

This value must be configured **before building the React application**.

The backend must also allow the frontend origin:

```text
CORS_ALLOWED_ORIGINS=https://<your-frontend-domain>
```

Multiple allowed origins can be comma-separated.

---

# 💾 Important Persistence Note

Mini Redis is primarily an **in-memory cache**.

The application provides:

```text
/save
/load
```

operations for file-based persistence.

However, Render's default filesystem is **ephemeral**.

This means files stored on the default Render filesystem should **not be considered durable across restarts or redeployments**.

For durable production storage, use:

- A persistent disk on an appropriate hosting service
- An external database
- An external persistent datastore

---

# 🧪 Verification

Run backend tests:

```bash
mvn clean test
```

Verify the React frontend:

```bash
cd mini-redis-ui
npm run lint
npm run build
```

A successful verification should complete:

```text
Backend Tests     ✅
Frontend Lint     ✅
Frontend Build    ✅
```

---

# 🎯 Key Concepts Demonstrated

This project demonstrates several important software development and backend concepts:

### 🧠 Data Structures & Algorithms

- Key-value storage
- LRU caching
- Cache eviction
- TTL expiration

### ☕ Java & Spring Boot

- Object-oriented programming
- Layered architecture
- REST controllers
- Dependency injection
- Exception handling
- Configuration management

### ⚡ Caching

- Cache hits
- Cache misses
- Hit ratio
- Active cache entries
- Cache capacity management

### 🔌 API Development

- REST APIs
- HTTP methods
- JSON request/response handling
- Standard API responses
- Swagger documentation

### ⚛️ Full-Stack Development

- React frontend
- Axios/API communication
- Vite development environment
- Frontend/backend integration

### 🐳 DevOps

- Maven
- npm
- Docker
- Multi-stage builds
- Render deployment
- Health monitoring

---

# ⚠️ Limitations

Mini Redis is an **educational Redis-inspired implementation**.

It does not provide the complete functionality of production Redis.

The current project does not aim to implement advanced Redis capabilities such as:

- Distributed clustering
- Replication
- High availability
- Redis Pub/Sub
- Redis wire protocol
- Advanced persistence strategies
- Full Redis data structures
- Enterprise-scale distributed caching

The main purpose of this project is to understand and demonstrate the **internal concepts behind in-memory caching and cache management**.

---

# 🔮 Future Enhancements

Possible future improvements include:

- 🔐 JWT authentication and authorization
- 🌐 Distributed cache support
- 🔄 Additional eviction strategies such as LFU and FIFO
- 📊 Advanced cache analytics
- 📈 Enhanced monitoring and metrics
- 💾 Durable external persistence
- 🚦 API rate limiting
- 🧪 Extended automated testing
- ⚙️ CI/CD pipeline
- 🗃️ Additional Redis-like data structures
- 📱 Improved responsive dashboard
- 🔔 Real-time cache activity monitoring

---

# 🤝 Contributing

Contributions are welcome.

### 1. Fork the repository

### 2. Create a feature branch

```bash
git checkout -b feature-name
```

### 3. Make your changes

### 4. Commit your changes

```bash
git commit -m "Add feature description"
```

### 5. Push your branch

```bash
git push origin feature-name
```

### 6. Open a Pull Request

---

# 👩‍💻 Author

## Sakshi Bhosale

💻 **GitHub**

https://github.com/sakshibhosale582

📂 **Project Repository**

https://github.com/sakshibhosale582/miniredis

🚀 **Live Project**

https://miniredis-274e.onrender.com/

---

# 📄 License

This project is licensed under the **MIT License**.

See the `LICENSE` file in the repository for the complete license text.

---

<div align="center">

# ⚡ Mini Redis

### Built with ☕ Spring Boot + ⚛️ React

*A practical Redis-inspired project for exploring in-memory caching, LRU eviction, TTL expiration, persistence, REST APIs, monitoring, and full-stack deployment.*

⭐ **If you find this project useful, consider starring the repository.**

</div>
