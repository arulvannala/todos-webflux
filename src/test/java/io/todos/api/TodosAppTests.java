package io.todos.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TodosAppTests {

    @Autowired
    private TodosProperties properties;

    @Test
    public void createDelete() {
        WebTestClient webTestClient = WebTestClient
            .bindToController(new TodosAPI(properties)).build();

        webTestClient.get().uri("/").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Todo.class)
                .consumeWith(body -> {
                    assertThat(body.getResponseBody()).isEmpty();
                });

        final String[] ids = new String[1];

        webTestClient.post()
            .uri("/")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(Todo.builder().title("make bacon pancakes").completed(false).build()), Todo.class)
            .exchange()
                .expectStatus().isCreated()
                .expectBody(Todo.class)
                .consumeWith( body -> {
                    assertThat(body.getResponseBody().getId()).isNotEmpty();
                    assertThat(body.getResponseBody().getTitle()).isEqualTo("make bacon pancakes");
                    assertThat(body.getResponseBody().getCompleted()).isFalse();
                    ids[0] = body.getResponseBody().getId();
                });

        webTestClient.get().uri("/").accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Todo.class)
            .consumeWith(body -> {
                body.getResponseBody().forEach(todo -> {
                    assertThat(todo.getId()).isNotEmpty();
                    assertThat(todo.getCompleted()).isFalse();
                });
            });

        webTestClient.get()
            .uri("/{id}", ids[0])
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Todo.class)
            .consumeWith( body -> {
                assertThat(body.getResponseBody().getId()).isEqualTo(ids[0]);
                assertThat(body.getResponseBody().getTitle()).isEqualTo("make bacon pancakes");
                assertThat(body.getResponseBody().getCompleted()).isFalse();
            });

        webTestClient.patch()
            .uri("/{id}", ids[0])
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(Todo.builder().completed(true).build()), Todo.class)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Todo.class)
            .consumeWith( body -> {
                assertThat(body.getResponseBody().getId()).isEqualTo(ids[0]);
                assertThat(body.getResponseBody().getTitle()).isEqualTo("make bacon pancakes");
                assertThat(body.getResponseBody().getCompleted()).isTrue();
            });

        webTestClient.delete()
            .uri("/{id}", ids[0])
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Todo.class)
            .consumeWith( body -> {
                assertThat(body.getResponseBody().getId()).isEqualTo(ids[0]);
                assertThat(body.getResponseBody().getTitle()).isEqualTo("make bacon pancakes");
                assertThat(body.getResponseBody().getCompleted()).isTrue();
            });

        webTestClient.get().uri("/").accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Todo.class)
            .consumeWith(body -> {
                assertThat(body.getResponseBody()).isEmpty();
            });

        webTestClient.get()
            .uri("/{id}", ids[0])
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    public void updateId() {
        WebTestClient webTestClient = WebTestClient
            .bindToController(new TodosAPI(properties)).build();

        webTestClient.get().uri("/").accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Todo.class)
            .consumeWith(body -> {
                assertThat(body.getResponseBody()).isEmpty();
            });

        final String[] ids = new String[1];

        webTestClient.post()
            .uri("/")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(Todo.builder().title("try to update the id field").completed(false).build()), Todo.class)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Todo.class)
            .consumeWith( body -> {
                assertThat(body.getResponseBody().getId()).isNotEmpty();
                assertThat(body.getResponseBody().getTitle()).isEqualTo("try to update the id field");
                assertThat(body.getResponseBody().getCompleted()).isFalse();
                ids[0] = body.getResponseBody().getId();
            });

        webTestClient.get()
            .uri("/{id}", ids[0])
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Todo.class)
            .consumeWith( body -> {
                assertThat(body.getResponseBody().getId()).isEqualTo(ids[0]);
                assertThat(body.getResponseBody().getTitle()).isEqualTo("try to update the id field");
                assertThat(body.getResponseBody().getCompleted()).isFalse();
            });

        // should accept update on valid resource but not update the id to hackId
        webTestClient.patch()
            .uri("/{id}", ids[0])
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(Todo.builder().id("hackId").completed(true).build()), Todo.class)
            .exchange()
            .expectStatus().isOk();

        webTestClient.get()
            .uri("/{id}", ids[0])
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Todo.class)
            .consumeWith( body -> {
                assertThat(body.getResponseBody().getId()).isEqualTo(ids[0]);
                assertThat(body.getResponseBody().getTitle()).isEqualTo("try to update the id field");
                assertThat(body.getResponseBody().getCompleted()).isTrue();
            });
    }
}