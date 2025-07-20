Feature: User Management API

  As a system user
  I want to manage users through a REST API
  So that I can perform CRUD operations on user data

  Scenario: Create a new user successfully
    Given the system is running
    When I create a user with name "John Doe" and email "john.doe@example.com"
    Then the user should be created successfully with status 201
    And the response should contain the user details

  Scenario: Retrieve a user by ID
    Given a user exists with name "Jane Doe" and email "jane.doe@example.com"
    When I retrieve the user by ID
    Then the user details should be returned with status 200
    And the response should contain "Jane Doe" as the name

  Scenario: Update an existing user
    Given a user exists with name "Mike" and email "mike@example.com"
    When I update the user's name to "Michael"
    Then the user should be updated successfully with status 200
    And the response should contain "Michael" as the updated name

  Scenario: Delete a user by ID
    Given a user exists with name "Alice" and email "alice@example.com"
    When I delete the user by ID
    Then the user should be deleted successfully with status 204
    And the user should no longer exist