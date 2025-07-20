# REST API - User Management

This project is a sample RESTful API built with Spring Boot for managing user data, demonstrating basic CRUD (Create, Retrieve, Update, Delete) operations. It also features a robust Behavior-Driven Development (BDD) testing framework using Cucumber and JUnit 5, integrated with Spring Boot's test capabilities.

## 🚀 Technologies Used

* **Java**: Version 17 (configured in `pom.xml`)
* **Spring Boot**: v3.5.3
    * Spring Boot Starter Web (for REST endpoints)
    * Spring Boot Starter Data JPA (for database interaction)
    * Spring Boot Starter Test (for unit and integration testing utilities)
* **Database**:
    * **MariaDB**: Used as the primary relational database for the application.
    * **H2 Database**: An in-memory database used exclusively for fast and isolated test execution.
* **Build Tool**: Apache Maven
* **Testing Framework**:
    * **Cucumber**: For BDD feature definitions and step execution.
    * **JUnit 5**: The testing framework.
    * **Mockito**: For mocking (if unit tests were added).
* **JDBC Driver**: MariaDB Java Client

## ✨ Features (API Endpoints)

The API exposes the following endpoints for user management:

| HTTP Method | Endpoint          | Description                    | Request Body Example (JSON)                                                       | Response                                |
| :---------- | :---------------- | :----------------------------- | :----------------------------------------                                         | :-------------------------------------- |
| `POST`      | `/save`           | Creates a new user             | `{ "firstName": "John", "lastName": "Doe", "age": 30, "occupation": "Engineer" }` | `201 Created` + User object (JSON)      |
| `GET`       | `/users/{id}`     | Retrieves a user by ID         | (None)                                                                            | `200 OK` + User object (JSON) or `404 Not Found` |
| `PUT`       | `/update/{id}`    | Updates an existing user by ID | `{ "firstName": "Jane", "lastName": "Smith", "age": 35 }` (all fields to update)  | `200 OK` + Updated User object (JSON) or `404 Not Found` |
| `DELETE`    | `/users/{id}`     | Deletes a user by ID           | (None)                                                                            | `204 No Content` or `404 Not Found`     |

## 🧪 Testing Framework (BDD with Cucumber)

The project includes a comprehensive BDD testing framework to ensure the API behaves as expected from a business perspective.

* **Feature Files:** Located in `src/test/resources/features/` and written in Gherkin syntax (e.g., `user_management.feature`). These define the high-level test scenarios.
* **Step Definitions:** Located in `src/test/java/com/crud/app.rest.steps/UserSteps.java`. These Java methods "glue" the Gherkin steps to the actual application code, using `TestRestTemplate` to interact with the running Spring Boot application during tests.
* **Spring Boot Integration:** The tests leverage Spring Boot's testing utilities (`@SpringBootTest`, `TestRestTemplate`) to spin up a full application context for integration testing.
* **H2 In-Memory Database:** Tests run against a clean H2 in-memory database instance
