package io.corbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@SpringBootApplication
public class TodosApp {

	public static void main(String[] args) {
		SpringApplication.run(TodosApp.class, args);
	}
}
