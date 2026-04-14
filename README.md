# Petalytics | E-Commerce Geo-Analytics Platform

**Petalytics** (from *Peta* - Indonesian for Map) is a pragmatic backend solution designed to solve a common problem in Indonesian e-commerce: **Wasted ad spend due to lack of geographic customer insights.**

[![CI Pipeline](https://github.com/YOUR_GITHUB_USERNAME/petalytics/actions/workflows/ci.yml/badge.svg)](https://github.com/YOUR_GITHUB_USERNAME/petalytics/actions/workflows/ci.yml)
[![Coverage](https://img.shields.io/badge/Coverage-92%25-brightgreen.svg)](https://github.com/YOUR_GITHUB_USERNAME/petalytics)
[![Java 21](https://img.shields.io/badge/Java-21-blue.svg)](https://jdk.java.net/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-61dafb.svg)](https://reactjs.org/)


## The Problem
Multi-channel marketplaces (Shopee, TikTok, etc.) provide raw order data but lack tools to visualize customer density. Sellers often "burn money" on broad ad targeting. **Petalytics** ingests raw marketplace exports and transforms them into actionable geographic intelligence.

## Key Features
- **Data Normalization:** Maps inconsistent Indonesian address strings into standardized regional data.
- **Multi-Platform Ingestion:** Unified processing for different marketplace schemas.
- **ROI Optimization:** Identifies high-density customer zones for precise ad targeting.

---

## Architectural Highlights

This project was engineered with a strict focus on **Clean Architecture, Scalability, and Data Integrity**, tailored for modern tech standards.

### Backend (Java 21 + Spring Boot 3)
- **Hexagonal Architecture (Ports & Adapters):** Strict separation of Domain logic from Infrastructure concerns.
- **High-Performance Ingestion:** Utilizes `FastExcel` and `Apache Commons CSV` with a streaming approach to parse 10MB+ export files with a microscopic JVM memory footprint.
- **Data Normalization Cache:** Prevents N+1 database queries during mass ingestion by loading PostgreSQL mapping rules (e.g., standardizing "DKI JAKARTA" and "Jakarta Province" into "DKI Jakarta") into a `ConcurrentHashMap` cache.
- **Idempotency:** Custom batch-query filtering prevents duplicate `OrderNo` ingestion, allowing safe, repeatable file uploads.
- **Robust Testing:** Adheres to **Test-Driven Development (TDD)** using JUnit 5, Mockito, and **Testcontainers** for true isolated PostgreSQL integration testing.

### Frontend (React + Vite)
- **Modern SPA:** Built with React, TypeScript, and Vite for blazing-fast Hot Module Replacement (HMR).
- **State Management:** Advanced React hook patterns (`useMemo`, `useEffect`) for client-side data aggregation, multi-column sorting, and debounced API filtering.
- **Data Visualization:** Interactive, highly responsive SVGs rendered via `Recharts`.
- **Tailwind CSS:** Custom design system mimicking modern SaaS aesthetics.

## Tech Stack

| Layer | Technologies                                                                   |
 |---|--------------------------------------------------------------------------------|
| **Backend** | Java 21, Spring Boot 3, Spring Data JPA, FastExcel, Apache Commons CSV, Flyway |
| **Frontend** | React 19, TypeScript, Vite, Tailwind CSS, Recharts, Lucide Icons               |
| **Database** | PostgreSQL 18 (Dockerized)                                                     |
| **DevOps** | GitHub Actions, Docker Compose, Testcontainers                                 |

---

## 🛠️ Local Development Setup

### Prerequisites
- Docker Desktop
- Java 21 (JDK)
- Node.js (v24+)

### 1. Start the Database
The infrastructure is defined as code. Spin up the isolated PostgreSQL container:
 ```bash
 cd petalytics-backend
 docker compose up -d
 ```
*(Note: Flyway will automatically run migrations and construct the schema upon application boot).*

### 2. Start the Backend API

```bash
cd petalytics-backend
./mvnw spring-boot:run
```
*The API will be available at `http://localhost:8080/api`*

### 3. Start the Frontend Dashboard
```bash
cd petalytics-frontend
npm install
npm run dev
```
*The UI will be available at `http://localhost:5173`*