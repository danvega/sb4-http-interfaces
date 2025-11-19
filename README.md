# Spring Boot 4 HTTP Interfaces Demo

A demonstration project showcasing the new HTTP Service Client features in Spring Framework 7 and Spring Boot 4. 
This project uses declarative HTTP interfaces to consume the [JSONPlaceholder API](https://jsonplaceholder.typicode.com/).

## What are HTTP Interfaces?

HTTP Interfaces allow you to define an HTTP service as a Java interface with annotated methods for HTTP exchanges. 
Spring Framework creates a proxy that implements the interface and performs the HTTP requests. This provides a type-safe, 
declarative approach to building HTTP clients.

```java
@HttpExchange("/todos")
public interface TodoService {

    @GetExchange
    List<Todo> findAll();

    @GetExchange("/{id}")
    Todo findById(@PathVariable Integer id);

    @PostExchange
    Todo create(@RequestBody Todo todo);
}
```

## Features

- **Declarative HTTP Clients** - Define HTTP services using Java interfaces
- **Type-Safe** - Full compile-time type checking for requests and responses
- **Annotation-Based** - Use `@HttpExchange`, `@GetExchange`, `@PostExchange`, `@PutExchange`, `@DeleteExchange`
- **RestClient Integration** - Built on Spring's modern RestClient
- **Spring Boot Auto-Configuration** - Minimal configuration required

## Prerequisites

- **Java 25**
- **Spring Boot 4.0.0+**
- **Spring Framework 7.0**
- **Maven 3.9+**

## Project Structure

```
src/main/java/dev/danvega/http/
├── Application.java              # Spring Boot entry point
├── ClientConfig.java             # HTTP client configuration
├── todo/
│   ├── Todo.java                 # Todo record model
│   ├── TodoService.java          # HTTP interface for JSONPlaceholder /todos
│   └── TodoController.java       # REST controller exposing /api/todos
└── post/
    ├── Post.java                 # Post record model
    ├── PostService.java          # HTTP interface for JSONPlaceholder /posts
    └── PostController.java       # REST controller exposing /api/posts

src/main/resources/
├── application.yaml              # Application configuration
├── todo.http                     # HTTP client tests for todos
└── post.http                     # HTTP client tests for posts
```

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd sb4-http-interfaces
```

### 2. Build the Project

```bash
./mvnw clean install
```

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`.

## Implementation Guide

### Step 1: Define Your Data Model

Use Java records for immutable data transfer objects:

```java
public record Todo(Integer id, Integer userId, String title, boolean completed) {
}
```

### Step 2: Create an HTTP Interface

Define methods for each HTTP operation using exchange annotations:

```java
@HttpExchange("/todos")
public interface TodoService {

    @GetExchange
    List<Todo> findAll();

    @GetExchange("/{id}")
    Todo findById(@PathVariable Integer id);

    @PostExchange
    Todo create(@RequestBody Todo todo);

    @PutExchange("/{id}")
    Todo update(@PathVariable Integer id, @RequestBody Todo todo);

    @DeleteExchange("/{id}")
    void delete(@PathVariable Integer id);
}
```

### Step 3: Configure the HTTP Client

Create a configuration class to set up RestClient and the proxy factory:

```java
@Configuration
public class ClientConfig {

    @Bean
    RestClient jsonplaceholderRestClient() {
        return RestClient.builder()
                .baseUrl("https://jsonplaceholder.typicode.com")
                .build();
    }

    @Bean
    HttpServiceProxyFactory jsonPlaceholderProxyFactory(RestClient jsonplaceholderRestClient) {
        return HttpServiceProxyFactory.builder()
                .exchangeAdapter(RestClientAdapter.create(jsonplaceholderRestClient))
                .build();
    }

    @Bean
    TodoService todoService(HttpServiceProxyFactory jsonPlaceholderProxyFactory) {
        return jsonPlaceholderProxyFactory.createClient(TodoService.class);
    }
}
```

### Step 4: Create a REST Controller

Expose the HTTP interface through your own REST API:

```java
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<Todo> findAll() {
        return todoService.findAll();
    }

    @GetMapping("/{id}")
    public Todo findById(@PathVariable Integer id) {
        return todoService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Todo create(@RequestBody Todo todo) {
        return todoService.create(todo);
    }

    @PutMapping("/{id}")
    public Todo update(@PathVariable Integer id, @RequestBody Todo todo) {
        return todoService.update(id, todo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        todoService.delete(id);
    }
}
```

## API Endpoints

### Todos

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/todos` | Get all todos |
| GET | `/api/todos/{id}` | Get todo by ID |
| POST | `/api/todos` | Create a new todo |
| PUT | `/api/todos/{id}` | Update an existing todo |
| DELETE | `/api/todos/{id}` | Delete a todo |

### Posts

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/posts` | Get all posts |
| GET | `/api/posts/{id}` | Get post by ID |
| POST | `/api/posts` | Create a new post |
| PUT | `/api/posts/{id}` | Update an existing post |
| DELETE | `/api/posts/{id}` | Delete a post |

## Testing with .http Files

The project includes `.http` files for testing endpoints using IntelliJ IDEA or VS Code (with REST Client extension).

### Example: todo.http

```http
### Get all todos
GET http://localhost:8080/api/todos
Accept: application/json

### Get todo by ID
GET http://localhost:8080/api/todos/1
Accept: application/json

### Create a new todo
POST http://localhost:8080/api/todos
Content-Type: application/json

{
  "userId": 1,
  "title": "Learn Spring Boot HTTP Interfaces",
  "completed": false
}
```

## Spring Framework 7 HTTP Client Enhancements

This project demonstrates the HTTP interface feature that was introduced in Spring Framework 6 and enhanced in Spring 
Framework 7. Key improvements include:

### Registry Layer & HTTP Service Groups

Spring Framework 7 introduces a registry layer over `HttpServiceProxyFactory` that provides:
- Configuration model to register HTTP interfaces and initialize HTTP client infrastructure
- Transparent creation and registration of client proxies as Spring beans
- Access to all client proxies via `HttpServiceProxyRegistry`

### @ImportHttpServices Annotation

Spring Framework 7 introduces `@ImportHttpServices` for declaratively registering HTTP service groups, reducing boilerplate configuration.

## Resources

- [HTTP Service Client Enhancements - Spring Blog](https://spring.io/blog/2025/09/23/http-service-client-enhancements)
- [JSONPlaceholder API](https://jsonplaceholder.typicode.com/)