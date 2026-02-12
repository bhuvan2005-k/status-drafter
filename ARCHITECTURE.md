# System Architecture - Status Drafter Tool

## Overview
The Status Drafter Tool is designed as a **monolithic Spring Boot application** following a **layered architecture**. This design ensures clear separation of concerns, ease of testing, and maintainability—key qualities for an interview-grade project.

## Layers
1. **Presentation Layer (Web/API)**:
   - Handles incoming HTTP requests.
   - Uses `REST Controllers` to define endpoints.
   - Employs `DTOs` (Data Transfer Objects) to decouple the internal domain model from the external API contract.
   - Implements `Global Exception Handling` using `@ControllerAdvice` for consistent error responses.

2. **Service Layer (Business Logic)**:
   - Contains the core logic for processing status updates.
   - **Parsing Engine**: A specialized component within the service layer that transforms unstructured text into structured objects.
   - Manages transactions and coordinates between different repositories.

3. **Data Access Layer (Persistence)**:
   - Uses `Spring Data JPA` to interact with the MySQL database.
   - `Repositories` provide an abstraction over CRUD operations and custom queries.

4. **Persistence Layer**:
   - **MySQL Database**: Stores normalized data including users, raw inputs, and parsed status details.

## Core Component: Parsing Engine
The Parsing Engine uses a **Rule-Based Tokenization** approach:
- **Tokenization**: Splits raw input into sentences and segments.
- **Classification**: Uses predefined (and DB-configurable) keywords (e.g., "accomplished", "planning to", "stuck") to map segments to categories: *Yesterday*, *Today*, *Blockers*.
- **Extraction**: Uses Regex to identify time durations (e.g., "2h", "45 mins").
- **Normalization**: Cleans filler words to ensure the final report is professional.

## Data Flow
1. User submits raw text via `POST /api/v1/status`.
2. Controller receives request and passes it to `StatusService`.
3. `ParsingService` processes the text and returns a structured `StatusMetadata` object.
4. `StatusService` saves the raw and structured data to the DB.
5. Structured JSON is returned to the user.

## Scalability & Extensibility
- **AI-Ready**: The parsing interface is designed to be swapped with an NLP/LLM service (e.g., OpenAI or LangChain) without changing the business logic.
- **Asynchronous Processing**: High-volume inputs can be handled using `@Async` or message queues (RabbitMQ/Kafka) in the future.
