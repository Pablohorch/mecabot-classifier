# API Reference for Mecabot-Classifier

This document provides a detailed reference for the APIs exposed by the Mecabot-Classifier microservice.

## Endpoints

### 1. Classify Repair Request

*   **Path:** `/api/classify`
*   **Method:** `POST`
*   **Description:** Analyzes a textual description of a mechanical problem and returns a classification of the repair category and an estimated time for completion. This is primarily achieved by leveraging the OpenAI API.
*   **Request Body:**
    *   **Content-Type:** `application/json`
    *   **Structure:**
        ```json
        {
          "problemDescription": "string"
        }
        ```
    *   **Fields:**
        *   `problemDescription` (string, mandatory): A textual description of the mechanical problem or repair request. Should be detailed enough for an AI to understand and classify. (e.g., "The car engine is making a strange rattling noise, especially when accelerating. It started last week and seems to be getting worse. No check engine light yet.")

*   **Responses:**

    *   **Success (200 OK):**
        *   **Content-Type:** `application/json`
        *   **Structure:**
            ```json
            {
              "category": "string",
              "estimatedTime": "string"
            }
            ```
        *   **Fields:**
            *   `category` (string): The determined category of the repair (e.g., "Engine Diagnosis", "Brake System Repair", "Electrical System Check").
            *   `estimatedTime` (string): The estimated time required for the repair (e.g., "2-4 hours", "1 day", "30 minutes").
        *   **Example:**
            ```json
            {
              "category": "Engine Diagnosis",
              "estimatedTime": "2-4 hours"
            }
            ```

    *   **Bad Request (400 Bad Request):**
        *   **Content-Type:** `application/json`
        *   **Description:** Occurs if the request payload is malformed, missing required fields (e.g., `problemDescription`), or if input validation fails (e.g., `problemDescription` is empty or too short/long if such validations are implemented).
        *   **Structure (Example - may vary based on error handling implementation):**
            ```json
            {
              "timestamp": "YYYY-MM-DDTHH:mm:ss.sssZ",
              "status": 400,
              "error": "Bad Request",
              "message": "Validation error: problemDescription must not be blank.", // Or other specific validation messages
              "path": "/api/classify"
            }
            ```

    *   **Internal Server Error (500 Internal Server Error):**
        *   **Content-Type:** `application/json`
        *   **Description:** Occurs if an unexpected error happens on the server side during processing, such as an issue communicating with the OpenAI API or an unhandled exception in the business logic.
        *   **Structure (Example - may vary based on error handling implementation):**
            ```json
            {
              "timestamp": "YYYY-MM-DDTHH:mm:ss.sssZ",
              "status": 500,
              "error": "Internal Server Error",
              "message": "An unexpected error occurred while processing the classification request.",
              "path": "/api/classify"
            }
            ```

*   **Notes:**
    *   The accuracy and specificity of the `category` and `estimatedTime` depend heavily on the capabilities of the configured OpenAI model and the clarity of the `problemDescription`.
    *   Error responses (4xx, 5xx) might have a more detailed structure if a global exception handler (`@ControllerAdvice`) is implemented.
