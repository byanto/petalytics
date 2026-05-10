# Product Requirements Document: Petalytics

## 1. Product Vision & Objective
**Petalytics** is an E-Commerce Geo-Analytics Platform designed to solve a common problem for online sellers: *wasted advertising spend due to a lack of geographic customer insights.*

**Strategic Goal:** 
Deliver a highly scalable, robust, and maintainable backend system capable of handling thousands of orders. The project prioritizes Clean Architecture (Pragmatic Hexagonal), Test-Driven Development (TDD), and performance optimization to ensure long-term business agility.

---

## 2. Target Audience
* **End-Users (E-commerce Sellers):** Need a fast, reliable way to upload messy marketplace exports and get clean visual insights into where their buyers live.
* **Engineering Team:** Requires a clean, well-tested, strictly-bounded, and maintainable codebase that allows for rapid feature iteration without technical debt.

---

## 3. Core Capabilities (Features)

### 3.1. Multi-Platform Order Ingestion
* **Description:** The system must accept raw `.csv` or `.xlsx` exports from various marketplaces (Shopee, TikTok, Tokopedia).
* **Requirements:**
  * Support streaming ingestion (e.g., Apache Commons CSV / FastExcel) to keep JVM memory footprint low (handling 10MB+ files efficiently).
  * Idempotency: Prevent duplicate order ingestion based on `OrderNo`.
  * Validate inputs strictly.

### 3.2. Data & Location Normalization
* **Description:** Address strings from marketplaces are notoriously messy (e.g., "DKI JAKARTA" vs "Jakarta Province"). The system must normalize these into a standardized format.
* **Requirements:**
  * Utilize an in-memory cache (`ConcurrentHashMap`) loaded from the database to map raw strings to standard locations, preventing N+1 DB queries during mass ingestion.

### 3.3. Geographic Analytics (Reporting)
* **Description:** Expose endpoints that aggregate data for the frontend dashboard.
* **Requirements:**
  * Provide `Location Summary` (total orders, total revenue per province/city).
  * Provide `Channel Summary` (performance per marketplace).
  * Support filtering by `Marketplace`, `StartDate`, and `EndDate`.

---

## 4. Non-Functional Requirements

1. **Architecture:** **Pragmatic Hexagonal Architecture (Ports & Adapters).**
   * *Trade-off Decision:* `jakarta.persistence` annotations will be used in the Domain layer to avoid mapping boilerplate, but `org.springframework` annotations are strictly forbidden in the Domain.
2. **Data Integrity:** Transactional boundaries must be strictly maintained at the Application Service layer.
3. **Testability:**
   * **Domain:** 100% unit test coverage using JUnit 5.
   * **Integration:** Testcontainers for PostgreSQL to test repository adapters.
   * No `@SpringBootTest` unless testing the outermost web adapter or full end-to-end flow.

---

## 5. Architectural Blueprint

The codebase will be restructured from "Package by Layer" to "Package by Bounded Context" to strictly enforce Ports & Adapters.

### Defined Contexts:
1. `ordering`: Handles ingestion, deduplication, and saving of raw orders.
2. `analytics`: Handles querying, aggregating, and reporting on the data.
3. `location`: Handles the normalization dictionaries and caching.

### Proposed Directory Structure (`com.budiyanto.petalytics.petalyticsbackend`)

```text
├── ordering
│   ├── domain               (Order, OrderItem, Marketplace) [JPA allowed, Spring forbidden]
│   ├── application          
│   │   ├── port             (In: IngestOrderUseCase, Out: OrderRepositoryPort, CsvParserPort)
│   │   └── service          (OrderIngestionService)
│   └── infrastructure       
│       ├── adapter          (In: OrderIngestionController | Out: PostgresOrderRepository, OpenCsvParser)
│
├── analytics
│   ├── domain               (Summary Models)
│   ├── application          (Queries & Ports)
│   └── infrastructure       (Read-only DB Adapters, Controllers)
│
└── common                   (Global Exception Handlers, Shared Utils, Base Entities)
```

---

## 6. Implementation Plan (Phased Refactoring)

* **Phase 1: Foundation Setup.** Create the new package structure (`ordering`, `analytics`, `common`, `location`).
* **Phase 2: Domain Migration.** Move `Order`, `OrderItem`, and `Marketplace` into `ordering.domain`. Ensure they encapsulate logic and only use JPA annotations.
* **Phase 3: Ports & Application.** Create Use Case interfaces (`port.in`) and SPI interfaces (`port.out`). Implement the Application Services to orchestrate them.
* **Phase 4: Adapters.** Move Controllers to `infrastructure.adapter.in` and Repositories/CSV Parsers to `infrastructure.adapter.out`. Wire them to the ports.
* **Phase 5: Cleanup & Test.** Delete old layered packages. Run tests, verify Flyway migrations, and test end-to-end ingestion.
