# Petalytics Architecture & Engineering Standards

## 1. Architectural Style
* **Pattern:** Pragmatic Hexagonal Architecture (Ports & Adapters).
* **The Dependency Rule:** Dependencies strictly point INWARD. `Infrastructure` -> `Application` -> `Domain`. The Domain has zero knowledge of the outside world.
* **Pragmatic Exception:** `jakarta.persistence.*` and Lombok annotations are allowed in the Domain layer to avoid mapping boilerplate. `org.springframework.*` is strictly forbidden in the Domain.

## 2. Naming Conventions
* **Interfaces (Ports):** Clean/UseCase naming. 
  * Inbound: `[Action]UseCase` (e.g., `IngestOrderUseCase`).
  * Outbound: `[Entity]RepositoryPort` or `[Action]Port` (e.g., `OrderRepositoryPort`).
* **Implementations:**
  * Application layer: `[Entity][Action]Service` (e.g., `OrderIngestionService`).
  * Infrastructure layer: `Postgres[Entity]Repository` or `Web[Entity]Controller`.

## 3. Core Development Rules
* **DTO Mapping:** Use **MapStruct**. No manual mapping unless specifically required for complex edge cases.
* **Error Handling:** Use global `@RestControllerAdvice` combined with Spring Boot 3's Problem Details (RFC 7807). No custom Result wrapper objects.
* **API Response:** Return raw JSON objects using `ResponseEntity<T>` with standard HTTP status codes. No legacy JSend wrappers (e.g., `{ "data": ... }`).
* **Validation:** "Bouncer and Vault" pattern. Validate fast at the Web layer using `jakarta.validation.Valid`, but *also* enforce invariants strictly inside Domain constructors.

## 4. Spring Boot & Java 21 Standards
* **Dependency Injection:** Constructor Injection ONLY. Use Lombok `@RequiredArgsConstructor` with `final` fields. No `@Autowired` on fields.
* **Imports:** Strictly `jakarta.*`. Never `javax.*`.
* **Java 21 Features:**
  * Use `record` for all DTOs, events, and immutable data structures.
  * Use `var` for local variable type inference where the type is obvious.
  * Prefer enhanced `switch` expressions over traditional statements.

## 5. Testing Standards (TDD)
* **Cycle:** Strict Red -> Green -> Refactor. Do not write production code without a failing test first.
* **Style:** BDD Style. Use `BDDMockito.given().willReturn()` instead of standard Mockito.
* **Assertions:** Use `AssertJ` fluent assertions (`assertThat()`).
* **Naming:** `given[Condition]_when[Action]_then[Result]()`. Use `@DisplayName("Should ...")` for readable test output.

## 6. Infrastructure & Git
* **Git Strategy:** Feature Branch Workflow (`feature/xxx`, `fix/xxx`). Never commit to `main`.
* **Commits:** Conventional Commits (`feat:`, `fix:`, `refactor:`). Commit ONLY on Green or Refactor phases.
* **Database (Flyway):** Strict immutability. Never modify an existing script. 
* **Integration Tests:** Test infrastructure adapters using **Testcontainers**.
