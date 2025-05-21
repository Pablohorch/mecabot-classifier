# AGENTS.md - README for AI Agents

This document provides guidelines and context for Large Language Model (LLM) agents to operate coherently and effectively on the Mecabot-Classifier project. Load these guidelines at the beginning of any "vibe coding" or "pair programming" session to enhance the quality of AI contributions. This is a living document and may evolve with the project.

## A. Contexto del Proyecto

*   **Ruta Principal del Código:** `/Users/pood/Personal/chatMeca/mecabot-classifier/`
*   **Estructura de Directorios Clave:**
    *   `pom.xml`: Maven project configuration (dependencies, plugins).
    *   `docker-compose.yml`: Docker environment setup.
    *   `project-settings.xml`: Maven settings specific to the project.
    *   `README.md`: Main project documentation.
    *   `src/main/java/com/mecabot/`: Root package for all Java application code.
        *   `MecabotClassifierApplication.java`: Spring Boot main application class.
        *   `controller/`: REST API controllers.
        *   `entity/`: JPA entities.
        *   `model/`: Data Transfer Objects (DTOs) and other non-persistent models.
        *   `repository/`: Spring Data JPA repositories.
        *   `service/`: Business logic services.
            *   `OpenAiClient.java`: Client for OpenAI API interaction.
    *   `src/main/resources/`: Application configuration and static resources.
        *   `application.yml`: Spring Boot configuration file (profiles, database, OpenAI API key placeholder).
        *   `data.sql`: Optional data initialization script.
    *   `src/test/java/com/mecabot/`: Test sources.
*   **Comandos Esenciales (ejecutar desde `/Users/pood/Personal/chatMeca/mecabot-classifier/` usando zsh):**
    *   Compilación Completa (saltando tests): `mvn --settings project-settings.xml clean install -DskipTests`
    *   Ejecución (modo desarrollo): `mvn --settings project-settings.xml spring-boot:run`
    *   Ejecución (desde JAR compilado): `java -jar target/mecabot-classifier-0.0.1-SNAPSHOT.jar`
    *   Ejecución con Docker (si `docker-compose.yml` está configurado): `docker-compose up -d`
    *   Limpiar proyecto: `mvn --settings project-settings.xml clean`
*   **Entorno de Desarrollo Principal:** Java 17+, Spring Boot 3.x, Apache Maven, macOS (shell zsh).
*   **Objetivo del Proyecto:** Mecabot-Classifier is a Spring Boot microservice to classify mechanical repair requests using the OpenAI API. It aims to return a repair categorization and a time estimate. The project seeks to expand its functionality, improve robustness, and integrate new services.

## B. Estándares de Estilo y Convenciones de Código

*   **Lenguaje Java:** Follow standard Java conventions and object-oriented programming best practices.
*   **Formato de Código:** Adhere to the existing code style. Use 4 spaces for indentation (no tabs).
*   **Uso de Lombok:** Utilize Lombok annotations (`@Data`, `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Service`, `@RestController`, `@Slf4j`, etc.) to reduce boilerplate code.
*   **Nomenclatura:**
    *   Clases e Interfaces: PascalCase (e.g., `ClassificationService`).
    *   Métodos y Variables: camelCase (e.g., `problemDescription`, `classifyRequest`).
    *   Constantes: UPPER_SNAKE_CASE (e.g., `DEFAULT_TIMEOUT_MS`).
    *   Paquetes: lowercase (e.g., `com.mecabot.controller`).
*   **Comentarios y JavaDoc:**
    *   Write clear and concise JavaDocs for all public classes, interfaces, and public methods, explaining their purpose, parameters (`@param`), return values (`@return`), and exceptions they might throw (`@throws`).
    *   Use inline comments (`//`) sparingly, only to explain complex or non-obvious logic.

## C. Plantillas y Guías para Mensajes de Commit y Pull Requests (PRs)

*   **Formato General de Mensaje de Commit:** `<type>(<scope>): <short concise description>`
    *   `<type>`: `feat` (new feature), `fix` (bug fix), `docs` (documentation changes), `style` (formatting, missing semi colons, etc., no logic change), `refactor` (code change that neither fixes a bug nor adds a feature), `test` (adding/modifying tests), `chore` (maintenance, build, etc.).
    *   `<scope>` (optional): Module or part of the code affected (e.g., `service`, `controller`, `openai`, `db`, `whatsapp`, `config`).
*   **Ejemplos de Mensajes de Commit:**
    *   `feat(service): Implement logic for WhatsApp API integration`
    *   `fix(openai): Correct parsing of response for GPT-4 Turbo model`
    *   `docs(readme): Update setup instructions for PostgreSQL`
    *   `refactor(OpenAiClient): Improve error handling and retries`
    *   `test(ClassificationService): Add tests for edge cases in classification`
*   **Pull Requests (PRs):** Must have a clear title and a description explaining the "what" and "why" of the changes. Reference GitHub issues if applicable.

## D. Instrucciones Personalizadas para el Agente LLM

*   "Prioritize the use of abstractions, annotations, and tools provided by the Spring Boot ecosystem (e.g., `@Value` for property injection, Spring profiles, Spring Data JPA, Spring Cache, RestTemplate/WebClient)."
*   "Generate unit tests (JUnit 5 / Mockito) for all new business logic or significant modifications. Ensure good coverage of success and error cases. Mock external dependencies (like OpenAI APIs, WhatsApp APIs, etc.) in unit tests."
*   "Keep JavaDoc documentation updated and complete for all public classes and methods modified or created."
*   "Do not make changes to `pom.xml` (add/remove dependencies or plugins) without clear justification and an explicit request from the user. If a dependency is added, explain its purpose."
*   "For any sensitive values (API keys, tokens, database passwords), ALWAYS use placeholders in the code and in `application.yml` (e.g., `YOUR_SECRET_HERE`, `DB_PASSWORD_PLACEHOLDER`). These values must be read from environment variables in production. Clearly indicate the expected environment variable name."
*   "When proposing modifications to existing files, clearly indicate which parts are new or modified, using comments like `// ...existing code...` to omit unaltered sections."
*   "Verify the existence of files and directories before proposing operations on them. Use available tools to list directories or read files if necessary to confirm the structure."
*   "Be explicit about the full path of files to be modified, e.g., `/Users/pood/Personal/chatMeca/mecabot-classifier/src/main/java/com/mecabot/service/OpenAiClient.java`. **Note for agent: This path needs to be relative to the repo root when using tools, e.g. `src/main/java/com/mecabot/service/OpenAiClient.java`**"
*   "If a new configuration is introduced in `application.yml`, explain each property and its purpose."
*   "When creating new classes or interfaces, place them in the appropriate package according to the project architecture (e.g., `com.mecabot.service`, `com.mecabot.model`, etc.)."
