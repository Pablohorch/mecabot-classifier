# OpenAI Integration Details for Mecabot-Classifier

This document outlines the specifics of the integration between Mecabot-Classifier and the OpenAI API.

## 1. Model(s) Utilized

*   **Primary Model:** The specific OpenAI model (e.g., `gpt-3.5-turbo`, `gpt-4`, `text-davinci-003`) is configurable via the `application.yml` file.
    *   Property: `openai.model`
    *   The system is designed to be flexible, but prompt engineering might need adjustments if the model is significantly changed.
*   **Default (if not specified):** (Assuming `gpt-3.5-turbo` is a common default, this should be confirmed from `application.yml` or `OpenAiClient.java` if a default is hardcoded). The `OpenAiClient.java` should be checked for the exact model used if not explicitly set in `application.yml`.

## 2. Prompt Structure and Construction

*   **Objective:** The primary goal of the prompt is to instruct the OpenAI model to analyze a user-provided problem description and return:
    1.  A concise repair category.
    2.  An estimated time for repair.
*   **Dynamic Construction:** The prompt is typically constructed dynamically within `OpenAiClient.java`. It usually involves:
    *   A system message or initial instruction setting the role/context for the AI (e.g., "You are an expert mechanic assistant...").
    *   The user's `problemDescription` seamlessly integrated into the prompt.
    *   Specific instructions on the desired output format (e.g., "Return your answer as a JSON object with keys 'category' and 'estimatedTime'." or a structured text format that the client can parse).

*   **Example Prompt Structure (Conceptual):**
    ```
    System: You are an expert automotive diagnostic AI. Given the user's description of a vehicle problem, identify a concise repair category and estimate the repair time. Provide your answer in the format:
    Category: [Your Identified Category]
    Estimated Time: [Your Estimated Time]

    User: The car engine is making a strange rattling noise, especially when accelerating. It started last week and seems to be getting worse. No check engine light yet.
    ```
    *(The actual prompt sent to the API might be a JSON structure for chat models like gpt-3.5-turbo, including roles like 'system' and 'user').*

## 3. API Key Management

*   **Configuration:** The OpenAI API key is managed externally and should not be hardcoded in the source code.
*   **Method:** It is injected into `OpenAiClient.java` via Spring's `@Value` annotation from a property in `application.yml`.
    *   Property in `application.yml`: `openai.api.key`
    *   Environment Variable: This property in `application.yml` is expected to resolve from an environment variable named `OPENAI_API_KEY`.
    *   Example in `application.yml`: `openai.api.key: ${OPENAI_API_KEY:YOUR_OPENAI_KEY_HERE_FOR_LOCAL_DEV_ONLY_NEVER_COMMIT_REAL_KEY}`
*   **Security:** This approach ensures that the actual API key is not committed to the repository. For production, the `OPENAI_API_KEY` environment variable must be securely set in the deployment environment.

## 4. Request and Response Handling

*   **Client:** `OpenAiClient.java` is responsible for:
    1.  Constructing the API request (including headers for authorization with the API key and content type).
    2.  Serializing the request body (e.g., to JSON).
    3.  Making the HTTP POST request to the OpenAI API endpoint (configurable via `openai.api.url` in `application.yml`).
    4.  Receiving the HTTP response.
    5.  Deserializing the JSON response from OpenAI.
    6.  Extracting the relevant information (category and estimated time) from the response. The exact parsing logic depends on the structure of the OpenAI model's response (e.g., parsing a JSON object, or extracting from a structured string).

*   **OpenAI API Endpoint:**
    *   Configured via `openai.api.url` in `application.yml`.
    *   Example: `https://api.openai.com/v1/chat/completions` (for chat models) or `https://api.openai.com/v1/completions` (for older completion models).

## 5. Error Handling

*   `OpenAiClient.java` should implement error handling for:
    *   **HTTP Errors:** Non-2xx responses from the OpenAI API (e.g., 401 Unauthorized for invalid API key, 429 Too Many Requests for rate limits, 5xx for OpenAI server errors).
    *   **Network Issues:** Problems connecting to the OpenAI API.
    *   **Response Parsing Errors:** If the OpenAI response is not in the expected format.
*   **Strategies:**
    *   Logging detailed error information.
    *   Throwing custom exceptions (e.g., `OpenAiApiException`) that can be handled by the `ClassificationService`.
    *   Implementing retry mechanisms (potentially with exponential backoff) for transient errors like rate limits or temporary server issues (this might be an advanced feature to add).

## 6. Token Usage and Cost Optimization

*   **Consideration:** Each call to the OpenAI API consumes tokens, which translates to cost.
*   **Current Status:** (To be filled in if specific strategies are implemented)
*   **Potential Strategies for Optimization:**
    *   **Prompt Engineering:** Crafting concise yet effective prompts to minimize input tokens.
    *   **Model Selection:** Choosing the most cost-effective model that meets the quality requirements.
    *   **Response Length Control:** Requesting the model to provide concise answers (e.g., using `max_tokens` parameter if applicable to the model).
    *   **Caching:** Implementing a cache (as per Task 4.3.D) to store and reuse responses for identical or very similar problem descriptions, reducing redundant API calls.

## 7. Future Enhancements

*   Support for more sophisticated prompt templating.
*   Dynamic model selection based on request characteristics.
*   More advanced retry and circuit breaker patterns for API calls.
*   Fine-tuning custom models (long-term).
```
