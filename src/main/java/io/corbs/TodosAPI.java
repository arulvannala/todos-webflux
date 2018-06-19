package io.corbs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("todos")
public class TodosAPI {

    @Value("${todos.webflux.limit}")
    private int limit;
    private final LinkedHashMap<Integer, Todo> todos = new LinkedHashMap<Integer, Todo>() {
        @Override
        protected boolean removeEldestEntry(final Map.Entry eldest) {
            return size() > limit;
        }
    };

    private static Integer seq = 0;

    @PostMapping("/")
    public Mono<Todo> create(@RequestBody Mono<Todo> todo) {
        return todo.map(it -> {
            it.setId(seq++);
            return it;
        }).map(it -> {
            todos.put(it.getId(), it);
            return it;
        }).map(it -> todos.get(it.getId()));
    }

    @GetMapping("/")
    public Flux<Todo> retrieve() {
        return Flux.fromIterable(todos.values());
    }

    @GetMapping("/{id}")
    public Mono<Todo> retrieve(@PathVariable Integer id) {
        return Mono.just(todos.get(id));
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

    @DeleteMapping("/")
    public void delete() {
        todos.clear();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        todos.remove(id);
    }


}