package io.todos.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@SpringBootApplication
@EnableConfigurationProperties(TodosProperties.class)
public class TodosApp {

	public static void main(String[] args) {
		SpringApplication.run(TodosApp.class, args);
	}
}
