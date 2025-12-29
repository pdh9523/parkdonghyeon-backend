# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java/site/donghyeon/bank`: Spring Boot entry point `BankApplication` and future domain layers.
- `src/main/resources/application.yml`: default configuration; keep secrets out of VCS.
- `src/test/java/site/donghyeon/bank`: JUnit 5 tests; mirror main package paths.
- Build assets: Gradle wrapper scripts (`gradlew`, `gradlew.bat`), root `build.gradle`, and `settings.gradle` with Java 17 toolchain.

## Build, Test, and Development Commands
- `./gradlew clean build` – compile sources, run all checks, and create a jar in `build/libs`.
- `./gradlew test` – run unit/integration tests; use `--tests '…ClassName'` to target specific suites.
- `./gradlew bootRun` – start the app locally using `application.yml`; good for manual API experiments.
- `./gradlew check` – aggregated verification; run before pushing or opening a PR.

## Coding Style & Naming Conventions
- Follow standard Java conventions: classes/records PascalCase, methods/fields camelCase, constants UPPER_SNAKE_CASE, packages lowercase under `site.donghyeon.bank`.
- Prefer 4-space indentation, one statement per line, and ordered imports without wildcards.
- Keep Spring stereotypes (`@Service`, `@Repository`, `@RestController`) adjacent to implementation classes; favor constructor injection.

## Testing Guidelines
- Stack: JUnit 5 with Spring Boot Test starter; default context lives in `BankApplicationTests`.
- Name test classes with `*Tests` and methods that describe behavior (e.g., `withdrawFailsWhenLimitExceeded`).
- Isolate domain logic with plain unit tests; reserve context-heavy `@SpringBootTest` cases for wiring and persistence.
- Run `./gradlew test` before commits; aim for coverage on new public behaviors and edge cases.

## Commit & Pull Request Guidelines
- No commit history yet; use concise, imperative subjects, preferably Conventional Commits, e.g., `feat: add transfer fee policy`.
- Keep PRs focused; include a summary, test evidence (`./gradlew test`), and note config or schema changes.
- Link related issues or README requirements; add screenshots or sample payloads for new APIs when helpful.

## Architecture Expectations
- Follow strict hexagonal architecture: domain core isolated, application services orchestrate use cases, and adapters handle I/O.
- Define ports (interfaces) in the domain/app layers; place implementations in adapter packages (e.g., persistence, web, messaging).
- Keep domain free of Spring/web/database dependencies; adapters may depend inward, never the reverse.
- Map requests/responses to domain DTOs at adapter boundaries; avoid leaking transport models into the core.

## Configuration & Security Notes
- Externalize credentials via environment variables or an ignored `application-local.yml`; never commit secrets to `application.yml`.
- Default dependencies include the PostgreSQL driver; document required DB/Redis endpoints in PR descriptions when they are introduced.
