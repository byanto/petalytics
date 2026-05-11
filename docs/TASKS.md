# Refactoring Roadmap

This document tracks the migration from Layered Architecture to Pragmatic Hexagonal Architecture.
Work in Micro-Tasks. Commit after each Green/Refactor cycle.

## Phase 1: Foundation Setup
- [X] Task 1.1: Create root bounded contexts: `ordering`, `analytics`, `location`, `common`.
- [X] Task 1.2: Add `MapStruct` dependency and configure it with `lombok-mapstruct-binding` in `pom.xml`.

## Phase 2: Domain Migration (Ordering)
- [X] Task 2.1: Move `Order`, `OrderItem`, and `Marketplace` to `ordering.domain.model`.
- [X] Task 2.2: Ensure the Domain contains NO Spring dependencies. Refactor to use Java 21 features if applicable.

## Phase 3: Ports & Application (Ordering)
- [X] Task 3.1: Define `IngestOrderUseCase` (Inbound Port) in `ordering.application.port.in`.
- [X] Task 3.2: Define `OrderRepositoryPort` and `CsvParserPort` (Outbound Ports) in `ordering.application.port.out`.
- [X] Task 3.3: Move/Refactor `OrderIngestionService` to implement `IngestOrderUseCase` using only the outbound ports.

## Phase 4: Adapters (Ordering)
- [X] Task 4.1: Move `OrderIngestionController` to `ordering.infrastructure.adapter.in.web`.
- [X] Task 4.2: Move `OrderRepository` (Spring Data interface) and create `PostgresOrderRepository` adapter in `ordering.infrastructure.adapter.out.persistence`.
- [X] Task 4.3: Refactor CSV parser to implement `CsvParserPort` in `ordering.infrastructure.adapter.out.parser`.

## Phase 5: Analytics & Location
- [X] Task 5.1: Apply Phase 2-4 steps to the Analytics context.
- [X] Task 5.2: Apply Phase 2-4 steps to the Location context.

## Phase 6: Polish
- [X] Task 6.1: Implement Global `@RestControllerAdvice`.
- [X] Task 6.2: Ensure 100% test coverage on Domain and Application layers.
- [ ] Task 6.3: Clean up empty legacy folders.
