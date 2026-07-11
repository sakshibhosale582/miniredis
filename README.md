# 🚀 Mini Redis

A modern in-memory cache system inspired by Redis, built using **Spring Boot** and **React**.

---

## Features

- Store Key-Value Pairs
- Retrieve Cached Data
- Delete Keys
- Clear Cache
- View Active Keys
- Command History
- Cache Statistics
- Auto Refresh Dashboard
- Swagger Documentation
- Responsive UI
- Exception Handling
- Unit Testing

---

## Tech Stack

### Backend

- Java 17
- Spring Boot
- Maven
- Swagger OpenAPI

### Frontend

- React
- Vite
- Bootstrap
- Axios
- React Icons
- React Toastify

---

## Project Structure

```
mini-redis
│
├── src
│   ├── cache
│   ├── commands
│   ├── controller
│   ├── dto
│   ├── response
│   └── exception
│
├── mini-redis-ui
│   ├── components
│   ├── services
│   ├── styles
│   └── assets
```

---

## REST APIs

| Method | Endpoint |
|---------|----------|
| POST | /cache/set |
| GET | /cache/get/{key} |
| DELETE | /cache/delete/{key} |
| GET | /cache/keys |
| GET | /cache/stats |
| GET | /cache/history |
| POST | /cache/clear |

---

## Swagger

```
http://localhost:8080/swagger-ui/index.html
```

---

## Run Backend

```bash
mvn spring-boot:run
```

---

## Run Frontend

```bash
cd mini-redis-ui
npm install
npm run dev
```

---

## Developed By

**SAKSHI BHOSALE**

MCA Final Project