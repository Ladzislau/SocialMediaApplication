# Social Media RESTful Application

Social Media RESTful Application is a RESTful API for a social media platform that allows users to register, log in, create posts, communicate with each other, subscribe to other users, and receive their activity feed.

## Documentation

- [OpenAPI YAML file](src/main/resources/openapi.yaml) - File with the OpenAPI specification in YAML format.

## Technologies

The Social Media RESTful Application is built using the following technologies:

- Spring Boot - Spring Boot provides a powerful framework for building Java applications with minimal configuration. The project uses version 3.1.0 of the Spring Boot starter parent.
- Spring Data JPA - This dependency enables the integration of Spring Data JPA for simplified database operations.
- Spring Security - Spring Security is used for user registration, authentication, and data privacy protection.
- Spring Web - This starter enables the development of web applications using Spring MVC.
- Java JWT - Java JWT is used for JWT (JSON Web Token) implementation.
- Hibernate Validator - Hibernate Validator is used for user data validation.
- Swagger - Swagger was used for API documentation generation.

## Features

- Registration and authentication with user data privacy protection, including password hashing and JWT usage, implemented using Spring Security.
- Users can create new publications by providing text, a title, and attaching images.
- Users can view posts from other users.
- Users can update and delete their own posts.
- Users can send friend requests to other users. Once a request is accepted, both users become friends. If a request is declined, the requesting user remains a follower.
- Users who are friends are also followers of each other.
- If one friend removes the other from their friends list, the second user remains a follower.
- Friends can send messages to each other.
- Users can request a conversation using an API endpoint.
- Subscriptions and activity feed:
  - The user's activity feed displays the publications from the users they are subscribed to.
  - The activity feed supports pagination and sorting by publication creation time.

## Getting Started

To get started with My Project, follow these steps:

1. Clone the repository: `git clone https://github.com/Ladzislau/SocialMediaApplication.git`
2. Install the necessary dependencies.
3. Build and run the project.
4. Explore the API documentation using the provided OpenAPI YAML file.

