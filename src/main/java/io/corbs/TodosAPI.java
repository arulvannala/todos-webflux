package io.corbs;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class TodosAPI {

    private Map<Integer, Todo> todos = new HashMap<>();

    private static Integer seq = 0;

    @GetMapping("/")
    public Flux<Todo> listTodos() {
        return Flux.fromIterable(todos.values());
    }

    @PostMapping("/")
    public Mono<Todo> createTodo(@RequestBody Mono<Todo> todo) {
        return todo.map(it -> {
            it.setId(seq++);
            return it;
        }).map(it -> {
           todos.put(it.getId(), it);
           return it;
        }).map(it -> todos.get(it.getId()));
    }

    @DeleteMapping("/")
    public void clean() {
        todos.clear();
    }

    @GetMapping("/{id}")
    public Mono<Todo> getTodo(@PathVariable Integer id) {
        return Mono.just(todos.get(id));
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable Integer id) {
        todos.remove(id);
    }

    @PatchMapping("/{id}")
    public Mono<Todo> update(@PathVariable Integer id, @RequestBody Mono<Todo> todo) {
        if(!todos.containsKey(id)) {
            return Mono.just(Todo.builder().build());
        }
        Todo old = todos.get(id);

        return todo.map(it -> {
            old.setCompleted(it.getCompleted());
            return it;
        }).map(it -> {
            if(!StringUtils.isEmpty(it.getTitle())){
                old.setTitle(it.getTitle());
            }
            return it;
        });
    }
}

