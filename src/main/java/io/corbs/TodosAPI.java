package io.corbs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;

@RestController
public class TodosAPI {

    private static final Logger LOG = LoggerFactory.getLogger(TodosAPI.class);

    private final Map<Integer, Todo> todos = Collections.synchronizedMap(new LinkedHashMap<>());

    private final static AtomicInteger seq = new AtomicInteger(1);

    private Integer limit;

    public TodosAPI(@Value("${todos.api.limit}") Integer limit) {
        LOG.info("TodosWebFlux booting with todos.api.limit=" + limit);
        this.limit = limit;
    }

    @PostMapping("/")
    public Mono<Todo> create(@RequestBody Mono<Todo> todo) {
        if(todos.size() < limit) {
            return todo.map(it -> {
                it.setId(seq.getAndIncrement());
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

    @GetMapping("/completed")
    public Flux<Todo> completed(@RequestParam(defaultValue = "0") Integer n) {
        return Flux.fromIterable(todos.values())
            .filter(Todo::getCompleted)
                .delayElements(Duration.ofMillis(n));
    }

    @GetMapping("/tag/{tag}")
    public Flux<Todo> tags(@PathVariable String tag) {
        return Flux.fromIterable(todos.values())
            .filter(todo -> todo.getTitle().contains(tag));
    }

    @GetMapping("/{id}")
    public Mono<Todo> retrieve(@PathVariable Integer id) {
        if(!todos.containsKey(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, format("todo.id=%d", id));
        }
        return Mono.just(todos.get(id));
    }

    @PatchMapping("/{id}")
    public Mono<Todo> update(@PathVariable Integer id, @RequestBody Mono<Todo> todo) {
        if(!todos.containsKey(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, format("todo.id=%d", id));
        }

        return todo.map(it -> {
            if(!ObjectUtils.isEmpty(it.getCompleted())) {
                todos.get(id).setCompleted(it.getCompleted());
            }
            if(!StringUtils.isEmpty(it.getTitle())){
                todos.get(id).setTitle(it.getTitle());
            }
            return todos.get(id);
        });
    }

    @DeleteMapping("/")
    public void delete() {
        todos.clear();
    }

    @DeleteMapping("/{id}")
    public Mono<Todo> delete(@PathVariable Integer id) {
        if(!todos.containsKey(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, format("todo.id=%d", id));
        }
        return Mono.just(todos.remove(id));
    }

    @GetMapping("/limit")
    public Mono<Limit> getLimit() {
        return Mono.just(Limit.builder().size(this.todos.size()).limit(this.limit).build());
    }

}
