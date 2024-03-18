# Lanki Server

## What is Lanki Server? 

Lanki Server is a Spring Boot application server to provide note creation and management features for enhanced productivity. This project uses microservice architecture, featuring an API Gateway (e.g., edge-service) responsible for user authentication and authorization, and rate limiting, as well as a resource server (e.g., api-service) serving REST API endpoints for note management. 

## Design 

### Software Architecture 

![image](https://github.com/imko/lanki-backend/assets/46854966/6cf04393-37ac-4a0e-b472-f25bedb018d7)

#### Edge Service (API Gateway) 

Edge service is an API Gateway, handling user authentication, authorization, and preventing API abuse via a rate limiting algorithm based on token bucket algorithm. It uses Spring Cloud Gateway to provide a single entry point to resources, utilizing features like routes, predicates, filters to direct HTTP requests to downstream service. Since this is a single point of entry, it's important to deploy at least two replicas of the edge service due to the risk of single point of failures. 

It utilizes Redis for session store with Spring Data Redis Reactive to handle session management and rate limiting in distributed system, and Keycloak for OAuth2 Authorizaiton Server which the edge service delegates authentication and token management to. As security is a topic that requires best practices, I have decided to use Keycloak which is open source which implies that the edge service is an OAuth2 client. 

Furthermore, I decided to use Resilience4j to improve the application robustness and reliability, which is crucial in distributed and microservices architectures. The main features of Resilience4j used in this project are circuit breakers and rate limiting. 

To make the application fault tolerant, I integrated Resilience4j to prevent any failure from cascading and affecting other components in the system, and combined circuit breakers with retries and time limiters. 

To briefly explain about the token bucket algorithm used for rate limiting, each user is assigned a bucket inside which tokens are dripped over time at a specific replenish rate. Each bucket has a maximum capacity and when a user makes a request, a token is removed from its bucket. When there are no more tokens left in the bucket, then the request is not permitted, and the user will have to wait until more tokens are dripped into its bucket. There is also a burst capacity to handle any spikes in requests. 


#### API Service (Resource Server) 

API service is an OAuth2 resource server which manages note creation and management for the application users. It uses Postgres as a database to store data and manages data with Spring Data JPA, specifically Hibernate implementations. Auditing data is also integrated and Flyway is used for database migrations. 

### Authentication

After a user successfully authenticates with Keycloak, the edge service receives an ID token and access token. The edge service extracts information about the authenticated user, sets up a context for the current user session to make it available to downstream services. The access token allows the edge service to call the downstream services on behalf of the user which the edge service will include the access token in all requests in HTTP header, which is called a token relay. The access token is stored in Redis to make the application stateless and scalable. 

## Tech Stacks 

- Java 17
- Spring Boot 3
- Spring Security 6
- Spring Data JPA
- Postgres
- Hibernate
- Flyway 
- Redis
- Keycloak
- Resilience4j
- Testcontainers
- JUnit
- Lombok
