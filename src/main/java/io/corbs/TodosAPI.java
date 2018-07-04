package io.corbs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.String.format;

@RestController
@RequestMapping("todos")
public class TodosAPI {

    @Value("${todos.api.limit}")
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

        if(todos.size() < limit) {
            return todo.log().map(it -> {
                it.setId(seq++);
                todos.put(it.getId(), it);
                return todos.get(it.getId());
            });
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                format("todos.api.limit=%d, todos.size()=%d", limit, todos.size()));
        }

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
            throw new RuntimeException("cannot update a todo with that id: " + id);
        }
        Todo old = todos.get(id);
        return todo.map(it -> {
            if(!ObjectUtils.isEmpty(it.getCompleted())) {
                old.setCompleted(it.getCompleted());
            }
            old.setCompleted(it.getCompleted());
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
