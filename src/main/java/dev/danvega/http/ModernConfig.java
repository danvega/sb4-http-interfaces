package dev.danvega.http;

import dev.danvega.http.post.PostService;
import dev.danvega.http.todo.TodoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
@ImportHttpServices(types = {TodoService.class, PostService.class})
public class ModernConfig {

    @Bean
    RestClientHttpServiceGroupConfigurer groupConfigurer() {
        return groups -> {
            groups.forEachClient((group,builder) -> builder
                    .baseUrl("https://jsonplaceholder.typicode.com/")
                    .build());
        };
    }

    /*
     * Example configuring multiple groups
     * @ImportHttpServices(group = "jsonplaceholder", types = {TodoService.class, PostService.class})
     */
    RestClientHttpServiceGroupConfigurer multipleGroupConfigurer() {
        return groups -> {
            groups.filterByName("github")
                    .forEachClient((group, b) -> b
                            .baseUrl("https://api.github.com")
                            .defaultHeader("Accept", "application/vnd.github.v3+json")
                            .build()
                    );

            groups.filterByName("jsonplaceholder")
                    .forEachClient((group, b) -> b
                            .baseUrl("https://jsonplaceholder.typicode.com/")
                            .build()
                    );
        };
    }

}
