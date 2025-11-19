package dev.danvega.http.todo;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class TraditionalTodoService {

    private final RestClient client;

    public TraditionalTodoService(RestClient.Builder builder) {
        this.client = builder
                .baseUrl("https://jsonplaceholder.typicode.com")
                .build();
    }

    public List<Todo> findAll() {
        return client.get()
                .uri("/todos")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public Todo findById(Integer id) {
        return client.get()
                .uri("/todos/{id}", id)
                .retrieve()
                .body(Todo.class);
    }

    public Todo create(Todo todo) {
        return client.post()
                .uri("/todos")
                .body(todo)
                .retrieve()
                .body(Todo.class);
    }

    public Todo update(Integer id, Todo todo) {
        return client.put()
                .uri("/todos/{id}", id)
                .body(todo)
                .retrieve()
                .body(Todo.class);
    }

    public void delete(Integer id) {
        client.delete()
                .uri("/todos/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}
