package io.corbs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TodosAppTests {

    @Test
    public void createDelete() {
        WebTestClient webTestClient = WebTestClient
                .bindToController(new TodosAPI(128)).build();
        webTestClient.post()
            .uri("/")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(Todo.builder().title("make bacon pancakes").completed(false).build()), Todo.class)
            .exchange()
                .expectStatus().isOk()
                .expectBody(Todo.class)
                .consumeWith( body -> {
                    assertThat(body.getResponseBody().getId()).isEqualTo(1);
                    assertThat(body.getResponseBody().getTitle()).isEqualTo("make bacon pancakes");
                    assertThat(body.getResponseBody().getCompleted()).isFalse();
                });

        webTestClient.delete()
            .uri("/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Todo.class)
                .consumeWith( body -> {
                    assertThat(body.getResponseBody().getId()).isEqualTo(1);
                    assertThat(body.getResponseBody().getTitle()).isEqualTo("make bacon pancakes");
                    assertThat(body.getResponseBody().getCompleted()).isFalse();
                });

        webTestClient.get()
                .uri("/{id}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(Todo.class);
    }
}