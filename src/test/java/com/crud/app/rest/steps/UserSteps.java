package com.crud.app.rest.steps;

import com.crud.app.rest.RestApiApplication; // Your main Spring Boot application class
import com.crud.app.rest.Models.User; // Your User entity
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate; // For making HTTP calls
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*; // For assertions

import java.util.HashMap;
import java.util.Map;

@CucumberContextConfiguration
@SpringBootTest(classes = RestApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class UserSteps {

    @Autowired
    private TestRestTemplate restTemplate; // Spring Boot's test utility for REST calls

    private ResponseEntity<User> lastResponse;
    private User createdUser;
    private long userId; // Store ID for retrieve/update/delete scenarios

    // --- Scenario: Create a new user successfully ---

    @Given("the system is running")
    public void the_system_is_running() {
        // SpringBootTest.WebEnvironment.RANDOM_PORT ensures the app is running
        // and TestRestTemplate is configured to hit it. No action needed here.
    }

    @When("I create a user with name {string} and email {string}")
    public void i_create_a_user_with_name_and_email(String name, String email) {
        User newUser = new User();
        String[] nameParts = parseName(name); // Use the helper
        newUser.setFirstName(nameParts[0]);
        newUser.setLastName(nameParts[1]);
//        newUser.setEmail(email); // Assuming you add an email field to your User entity

        // Make the POST request to your API
        lastResponse = restTemplate.postForEntity("/save", newUser, User.class);
        createdUser = lastResponse.getBody(); // Store the created user for later checks
        if (createdUser != null) {
            userId = createdUser.getId();
        }
    }

    private String[] parseName(String fullName) {
        String[] parts = fullName.split(" ", 2); // Split into at most 2 parts
        String firstName = parts[0];
        String lastName = (parts.length > 1) ? parts[1] : ""; // Default to empty string if no last name
        return new String[]{firstName, lastName};
    }

    @Then("the user should be created successfully with status {int}") // Changed {string} to {int}
    public void the_user_should_be_created_successfully_with_status(int expectedStatusCode) {
        assertEquals(expectedStatusCode, lastResponse.getStatusCode().value(),
                "Expected status code for creation"); // Add message for clarity
        assertNotNull(createdUser); // Ensure createdUser is not null
        assertNotNull(createdUser.getId()); // Ensure it has an ID
    }

    @Then("the response should contain the user details")
    public void the_response_should_contain_the_user_details() {
        assertNotNull(createdUser.getFirstName());
        assertNotNull(createdUser.getLastName());
//        assertNotNull(createdUser.getEmail());
        assertTrue(createdUser.getId() > 0);
    }

    // --- Scenario: Retrieve a user by ID ---

    @Given("a user exists with name {string} and email {string}")
    public void a_user_exists_with_name_and_email(String name, String email) {
        User newUser = new User();
        String[] nameParts = parseName(name); // Use the helper
        newUser.setFirstName(nameParts[0]);
        newUser.setLastName(nameParts[1]); // This is now safe
//        newUser.setEmail(email);

        ResponseEntity<User> response = restTemplate.postForEntity("/save", newUser, User.class);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value(), "User creation status check in Given");
        createdUser = response.getBody();
        assertNotNull(createdUser, "Created user should not be null in response for Given");
        assertNotNull(createdUser.getId(), "Created user ID should not be null in Given");
        userId = createdUser.getId(); // <--- CRITICAL: ENSURE userId IS CORRECTLY CAPTURED AND ASSIGNED HERE.

// Instead, you need to set the createdUser and userId variables for later steps:
// You can add more assertions here to verify the created user's data:
// assertEquals(newUser.getFirstName(), createdUser.getFirstName());
// assertEquals(newUser.getLastName(), createdUser.getLastName());
// etc.
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        userId = response.getBody().getId(); // Store the ID for the next step
    }

    @When("I retrieve the user by ID")
    public void i_retrieve_the_user_by_id() {
        lastResponse = restTemplate.getForEntity("/users/" + userId, User.class); // Ensure it uses the captured userId
    }

    @Then("the user details should be returned with status {int}")
    public void the_user_details_should_be_returned_with_status(int expectedStatusCode) {
        // Change the assertion to compare integer values:
        assertEquals(expectedStatusCode, lastResponse.getStatusCode().value(),
                "Expected status code to be " + expectedStatusCode + " but was " + lastResponse.getStatusCode().value());
        // ... (other assertions in this step)
    }

    @Then("the response should contain {string} as the name")
    public void the_response_should_contain_as_the_name(String expectedName) {
        // This will compare the full name, adjust if you only expect firstName
        assertEquals(expectedName.split(" ")[0], lastResponse.getBody().getFirstName());
        assertEquals(expectedName.split(" ")[1], lastResponse.getBody().getLastName());
    }

    // --- Scenario: Update an existing user ---

    @When("I update the user's name to {string}")
    public void i_update_the_user_s_name_to(String newName) {
        // Retrieve the existing user first to get its current state and ID
        ResponseEntity<User> existingUserResponse = restTemplate.getForEntity("/users/" + userId, User.class);
        assertEquals(HttpStatus.OK, existingUserResponse.getStatusCode());
        User userToUpdate = existingUserResponse.getBody();
        assertNotNull(userToUpdate);

        // Update the name
        String[] newNameParts = parseName(newName);
        userToUpdate.setFirstName(newNameParts[0]);
        userToUpdate.setLastName(newNameParts[1]); // This should now be safe

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> requestEntity = new HttpEntity<>(userToUpdate, headers);
        // Send the PUT request (assuming your update endpoint uses PUT as recommended)
        restTemplate.put("/update/" + userId, userToUpdate); // No response body typically for PUT
        // Re-retrieve to verify update
        lastResponse = restTemplate.exchange("/update/" + userId, HttpMethod.PUT, requestEntity, User.class);
    }


    @Then("the user should be updated successfully with status {int}")
    public void the_user_should_be_updated_successfully_with_status(int expectedStatusCode) {
        // Change the assertion to compare integer values:
        assertEquals(expectedStatusCode, lastResponse.getStatusCode().value(),
                "Expected status code to be " + expectedStatusCode + " but was " + lastResponse.getStatusCode().value());
        // ... (other assertions in this step)
    }

    @Then("the response should contain {string} as the updated name")
    public void the_response_should_contain_as_the_updated_name(String updatedName) {
        String[] expectedNameParts = parseName(updatedName);
        assertEquals(expectedNameParts[0], lastResponse.getBody().getFirstName());
        assertEquals(expectedNameParts[1], lastResponse.getBody().getLastName()); // This should now be safe
    }

    // --- Scenario: Delete a user by ID ---

    @When("I delete the user by ID")
    public void i_delete_the_user_by_id() {
        // Use exchange to capture the response for a DELETE operation
        lastResponse = restTemplate.exchange("/users/" + userId, HttpMethod.DELETE, null, User.class);
        // Even if the response is 204 No Content, lastResponse will be a non-null ResponseEntity with the correct status
    }

    @Then("the user should be deleted successfully with status {int}")
    public void the_user_should_be_deleted_successfully_with_status(int expectedStatusCode) {
        // Assuming lastResponse was set by a previous delete operation (e.g., restTemplate.exchange or restTemplate.delete)
        // If restTemplate.delete() is used, it often returns void for a successful 204.
        // You might need to change your delete API endpoint to return ResponseEntity<Void> or a message.
        // Or, for simple deletion, often the check is just that the subsequent GET returns 404.

        // If your delete endpoint returns 200/204, capture it.
        // For simplicity, let's assume `lastResponse` from a check after delete.

        // If you're testing an endpoint that returns a ResponseEntity:
        // assertEquals(expectedStatusCode, lastResponse.getStatusCode().value(), "Expected status code for delete");

        // If your @DeleteMapping endpoint returns `void` (common for 204 No Content),
        // you cannot check `lastResponse.getStatusCode().value()`.
        // The common pattern is to:
        // 1. Perform the DELETE operation.
        // 2. In the NEXT step ("And the user should no longer exist"), assert 404 on GET.
        // So, this step might just be a placeholder or check `HttpStatus.NO_CONTENT.value()` if your API returns 204.
        // For now, let's keep it simple and just make it pass if the delete itself didn't throw an error.
        // You might need to adjust your @DeleteMapping to return ResponseEntity<Void> for status checks.
        // OR simply remove this step if the "no longer exist" step is sufficient.

        // For now, to make the test pass, you might just do:
        // assertDoesNotThrow(() -> {
        //    // your delete logic was already performed in @When
        // });
        // But ideally, the delete method itself returns a ResponseEntity.

        // If your @DeleteMapping returns `ResponseEntity<Void>`:
        // You need to capture the response from the delete operation.
        // In @When("I delete the user by ID"):
        // lastResponse = restTemplate.exchange("/users/" + userId, HttpMethod.DELETE, null, User.class); // Use exchange for delete and capture response
        // In this @Then step:
        // assertEquals(expectedStatusCode, lastResponse.getStatusCode().value(), "Expected status code for delete");

        // Let's assume your @DeleteMapping returns ResponseEntity<Void> for now,
        // so `lastResponse` would hold the response from the `exchange` call.
        // Change the assertion to compare integer values:
        assertEquals(expectedStatusCode, lastResponse.getStatusCode().value(),
                "Expected status code to be " + expectedStatusCode + " but was " + lastResponse.getStatusCode().value());
        // ... (other assertions in this step)
    }

    @Then("the user should no longer exist")
    public void the_user_should_no_longer_exist() {
        ResponseEntity<User> response = restTemplate.getForEntity("/users/" + userId, User.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()); // Expect 404 Not Found
    }

    // You will need to add corresponding endpoints to your RestApiControllers
    // For example:
    // @GetMapping("/users/{id}") for retrieve
    // @DeleteMapping("/users/{id}") for delete
    // @PutMapping("/update/{id}") if you follow best practices
}