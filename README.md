## Todo(s) Webflux Backing API

Howdy and welcome.  This repository contains a Reactive API implemented in Spring Boot 2.0 and Spring Cloud.  This API uses Spring WebFlux to map endpoints to methods using the time tested ``@RestController`` and ``@RequestMapping`` annotations.  This API is a backend for the [Vue.js version](http://todomvc.com/examples/vue/) of [TodoMVC](http://todomvc.com/).  Frontend repo is [here](https://github.com/corbtastik/todos-ui).

### Primary dependencies

* Spring Boot Starter Web (implement API)
* Spring Boot Actuators (ops endpoints)
* Spring Cloud Netflix Eureka Client (service discovery)
* Spring Cloud Config Client (central config)
* Spring Cloud Sleuth (request tracing)

### Primary Features